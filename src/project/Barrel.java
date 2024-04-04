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
import java.util.List;
import java.util.Map;

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
    public static Map<String, Map<String, UrlInfo>> invertedIndex = new HashMap<>();

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
                    // Multicast can send an ack that the word already exists
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

    public String getUrl(String url) throws RemoteException {
        // for (String msg : index) {
        //     System.out.println(msg);
        // }
        // if (index.containsKey(url)) {
        //     return url;
        // }
        return "";
    }

    // Search for URLs containing a given term
    public List<UrlInfo> searchTop10(String term) throws RemoteException {
        Map<String, UrlInfo> urlFrequency = invertedIndex.getOrDefault(term, Collections.emptyMap());

        // Create a list of URLs sorted by frequency of term occurrences
        List<UrlInfo> sortedUrls = new ArrayList<>(urlFrequency.values());
        sortedUrls.sort((url1, url2) -> url2.termFrequency - url1.termFrequency);
        return sortedUrls;
    }
}

