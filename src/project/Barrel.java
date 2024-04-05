package project;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import project.interfaces.Barrel_C_I;
import project.interfaces.Barrel_I;
import project.resources.Message;
import project.resources.UrlInfo;
import project.resources.WebPage;

public class Barrel extends UnicastRemoteObject implements Barrel_C_I
{
    // Multicast
    static String MULTICAST_ADDRESS = "230.0.0.1";
    static int MULTICAST_PORT = 4446;

    // Barrel variables
    String name;
    int port;

    //  mais relevante se tiver mais ligações **de** outras páginas
    // invertedIndex:
    //              word : Map<url, UrlInfo>
    public static Map<String, Map<String, UrlInfo>> invertedIndex = new ConcurrentHashMap<>();

    public Barrel() throws RemoteException {
        super();
    }

    public static void main(String[] args) {
        try {
            // create new file
            // index will save all current data taken from downloader
            // once indexing is done, we save to the file?

            Barrel_I server = null;
            // Must make this also a unicast stuff RMI
            // Try and give correct error messages and such
            try {
                try {
                    server = (Barrel_I) Naming.lookup("rmi://localhost:1097/barrel");
                    // SUBSCRIBE BARREL TO SERVER!!!
                    Barrel myself  = new Barrel();
                    server.subscribeBarrel((Barrel_C_I) myself);
                }
                catch(MalformedURLException e) {
                    System.out.println("Server Url is incorrectly formed");
                    System.out.println("Cant comunicate with server...");
                    System.out.println("Closing...");
                    System.exit(0);
                }
                catch (NotBoundException e) {
                    System.out.println("Cant comunicate with server...");
                    System.out.println("Closing...");
                    System.exit(0);
                }
                System.out.println("Barrel is ready");
            } catch (RemoteException e) {
                System.out.println("Error comunicating to the server");
                System.exit(0);
            }

            MulticastSocket multicastSocket = null;
            try {
                multicastSocket = new MulticastSocket(MULTICAST_PORT);
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                multicastSocket.joinGroup(new InetSocketAddress(group, 0), NetworkInterface.getByIndex(0));

                while (true) 
                {
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    multicastSocket.receive(packet);

                    //UrlQueue Element
                    ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData());
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    Message msg2receive = null;
                    try {
                        msg2receive = (Message) ois.readObject();
                    } catch (EOFException e) {
                        ois.close();
                        continue;
                    }
                    ois.close();

                    System.out.println(msg2receive);
                    String word = msg2receive.word;
                    WebPage webpage = msg2receive.page;

                    // add if word does not exist
                    invertedIndex.putIfAbsent(word, new HashMap<>());
                    // get the Map of the word
                    Map<String, UrlInfo> urlMap = invertedIndex.get(word);
                    urlMap.putIfAbsent(
                        webpage.url,
                        new UrlInfo(webpage.url, webpage.title, webpage.citation, 0)
                    );
                    UrlInfo info = urlMap.get(webpage.url);
                    // Increment frequency
                    info.termFrequency += 1; 
                    // Add url to pages referencing this page
                    info.urlsPointing2this.add(webpage.fatherUrl);
                }
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                multicastSocket.close();
            }
        }
        catch (Exception e) {
            System.out.println("Exception in main: " + e);
        }
    }

    public List<String> getUrlsConnected2this(String url) throws RemoteException {
        List<String> list = new ArrayList<>();
        Set<String> strings = new HashSet<>();
        for (Map<String, UrlInfo> innerMap : invertedIndex.values()) {
            for (String key : innerMap.keySet()) {
                if (key.equals(url)) {
                    List<String> temp = innerMap.get(key).urlsPointing2this;
                    for (String page : temp) {
                        if (!strings.contains(page)) {
                            list.add(page);
                            strings.add(page);
                        }
                    }
                }
            }
        }
        return list;
    }

    // Search for URLs containing a given term
    public List<UrlInfo> searchTop10(String[] term) throws RemoteException {
        // Currently this only sorts by frequency
        // Add smth to make it also sort if an url has more of the intented words in it
        // More terms = higher priority
        List<UrlInfo> list = new ArrayList<>();
        for (int i = 1; i < term.length; i++) {
            Map<String, UrlInfo> urlFrequency = invertedIndex.getOrDefault(term[i], Collections.emptyMap());
            List<UrlInfo> urls = new ArrayList<>(urlFrequency.values());
            list.addAll(urls);
        }
        list.sort((url1, url2) -> url2.termFrequency - url1.termFrequency);
        return list;
    }
}

