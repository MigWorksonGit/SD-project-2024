package project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import project.interfaces.Barrel_C_I;
import project.interfaces.Barrel_I;
import project.beans.Message;
import project.beans.UrlInfo;
import project.beans.WebPage;

public class Barrel extends UnicastRemoteObject implements Barrel_C_I
{
    // Multicast
    static String MULTICAST_ADDRESS = "230.0.0.1";
    static int MULTICAST_PORT = 4446;

    // Barrel variables
    static Barrel_I server = null;
    static String name;
    public void setName(String newName) throws RemoteException {
        name = newName;
    }
    public String getName() throws RemoteException {
        return name;
    }
    // Average time
    double averageExecutionTime = 0;
    public double getAvgExeTime() throws RemoteException {
        return averageExecutionTime;
    }

    //  mais relevante se tiver mais ligações **de** outras páginas
    // invertedIndex:
    //              word : Map<url, UrlInfo>
    public static Map<String, Map<String, UrlInfo>> invertedIndex = new ConcurrentHashMap<>();

    public Barrel() throws RemoteException {
        super();
    }

    public static void main(String[] args) {
        // Dont forget to check if stuff is valid
        String filepath = "config/config.json";
        String IP = null;
        String PORT = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filepath));
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(bufferedReader, JsonObject.class);
            IP = json.get("IpAddress").getAsString();
            PORT = json.get("Port").getAsString();
        } catch (Exception e) {
            System.out.println("Json file does not exist");
            System.exit(0);
        }
        // RMI connection
        String lookup = "rmi://" + IP + ":" + PORT + "/barrel";
        // Detect shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // System.out.println("HAHAHA");
            try {
                if (name != null) {
                    int index = Character.getNumericValue(name.charAt(name.length() - 1));
                    server.removeBarrel(index);
                }
            } catch (RemoteException e) {
                ;
            }
        }));
        int reconnect = 0;
        while (reconnect < 3) {
            // Connect to server
            try {
                server = null;
                // Must make this also a unicast stuff RMI
                // Try and give correct error messages and such
                try {
                    try {
                        server = (Barrel_I) Naming.lookup(lookup);
                        // SUBSCRIBE BARREL TO SERVER!!!
                        Barrel myself  = new Barrel();
                        server.subscribeBarrel((Barrel_C_I) myself);
                        MULTICAST_ADDRESS = server.getMulticastAddress();
                        MULTICAST_PORT = server.getMulticastPort();
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
                    System.out.println("Barrel " + name + " is ready");
                } catch (RemoteException e) {
                    System.out.println("Error comunicating to the server");
                    System.exit(0);
                }

                // create or check if file exists
                String folderPath = "src/main/java/project/barrel_files";
                String filename = name + ".txt";
                File folder = new File(folderPath);
                if (!folder.exists())
                    folder.mkdirs();
                File file = new File(folder, filename);
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        System.out.println("Could not create file. Exiting out...");
                        System.exit(0);
                    }
                }
                else {
                    // unusual line terminators?
                    try {
                        FileReader fReader = new FileReader(file);
                        BufferedReader bReader = new BufferedReader(fReader);
                        String line;
                        while((line = bReader.readLine()) != null) {
                            String[] parts = line.split("\\|");
                            if (parts.length == 6) {
                                String word1 = parts[0];
                                String url1 = parts[1];
                                String title1 = parts[2];
                                String citation1 = parts[3];
                                int frequency1 = Integer.parseInt(parts[4]);
                                String father1 = parts[5];
                                invertedIndex.putIfAbsent(word1, new HashMap<>());
                                Map<String, UrlInfo> urlMap = invertedIndex.get(word1);
                                urlMap.putIfAbsent(
                                    url1,
                                    new UrlInfo(url1, title1, citation1, frequency1)
                                );
                                UrlInfo info = urlMap.get(url1);
                                // Increment frequency
                                info.termFrequency += 1; 
                                // Add url to pages referencing this page
                                info.urlsPointing2this.add(father1);
                            }
                        }
                        bReader.close();
                    } catch (IOException e) {
                        System.out.println("Could not read from file. Exiting out...");
                        System.exit(0);
                    }
                }

                FileWriter fWriter = new FileWriter(file, true);
                BufferedWriter bWriter = new BufferedWriter(fWriter);

                MulticastSocket multicastSocket = null;
                try {
                    multicastSocket = new MulticastSocket(MULTICAST_PORT);
                    InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                    multicastSocket.joinGroup(new InetSocketAddress(group, 0), NetworkInterface.getByIndex(0));

                    while (true) 
                    {
                        // Receive packet
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
                        String word = msg2receive.getWord();
                        WebPage webpage = msg2receive.getPage();

                        // add if word does not exist
                        invertedIndex.putIfAbsent(word, new HashMap<>());
                        // get the Map of the word
                        Map<String, UrlInfo> urlMap = invertedIndex.get(word);
                        urlMap.putIfAbsent(
                            webpage.getUrl(),
                            new UrlInfo(webpage.getUrl(), webpage.getTitle(), webpage.getCitation(), 0)
                        );
                        UrlInfo info = urlMap.get(webpage.getUrl());
                        // Increment frequency
                        info.termFrequency += 1; 
                        // Add url to pages referencing this page
                        info.urlsPointing2this.add(webpage.getFatherUrl());
                        // put back into map
                        urlMap.put(webpage.getUrl(), info);
                        invertedIndex.put(word, urlMap);

                        // Writing to file
                        bWriter.write(word + "|" + webpage.getUrl() + "|" + webpage.getTitle() + "|" + webpage.getCitation() + "|" + info.termFrequency + "|" + webpage.getFatherUrl());
                        bWriter.newLine();

                        // Send ACK that packet was received suceffuly
                        String ack = "ACK";
                        byte[] bufferAck = ack.getBytes();
                        DatagramPacket pack2send = new DatagramPacket(bufferAck, bufferAck.length, packet.getAddress(), packet.getPort());
                        multicastSocket.send(pack2send);
                    }
                }
                catch (IOException e) {
                    System.out.println("Error in reading file " + e);
                } finally {
                    multicastSocket.close();
                    bWriter.close();
                    reconnect++;
                }
            }
            catch (Exception e) {
                System.out.println("Exception in main: " + e);
                reconnect++;
            }
        }
        System.out.println("Recconect has failed with " + reconnect + " tries");
        System.exit(0);
    }

    public List<String> getUrlsConnected2this(String url) throws RemoteException {
        LocalTime startTime = LocalTime.now(); // start timer
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
        LocalTime endTime = LocalTime.now(); // end timer
        Duration duration = Duration.between(startTime, endTime);
        long deciseconds = duration.toMillis() / 100;
        averageExecutionTime = (averageExecutionTime + deciseconds) / 2;
        return list;
    }

    // Search for URLs containing a given term
    public List<UrlInfo> searchTop10_BarrelPartition(String[] term, int page) throws RemoteException {
        LocalTime startTime = LocalTime.now(); // start timer
        List<UrlInfo> list = new ArrayList<>();
        for (int i = 1; i < term.length; i++) {
            Map<String, UrlInfo> urlFrequency = invertedIndex.getOrDefault(term[i], Collections.emptyMap());
            List<UrlInfo> urls = new ArrayList<>(urlFrequency.values());
            list.addAll(urls);
        }
        // If there is only one word to search for
        if (term.length == 2) {
            list.sort((url1, url2) -> url2.termFrequency - url1.termFrequency);
            // create new list
            List<UrlInfo> finalList = new ArrayList<>();
            for (int i = page; i < Math.min(list.size(), page+10); i++) {
                finalList.add(list.get(i));
            }
            // timer stuff
            LocalTime endTime = LocalTime.now(); // end timer
            Duration duration = Duration.between(startTime, endTime);
            double deciseconds = duration.toMillis() / 100;
            averageExecutionTime = (averageExecutionTime + deciseconds) / 2;
            return finalList;
        }
        // else, if more than 1 word
        // DO NOT COMBINE the values of duplicate urls -> choose only those the those 2 words
        List<UrlInfo> duplicateList = new ArrayList<>();
        // Object[] { UrlInfo, true or false, true if its duplicate }
        Map<String, Object[]> dupMap = new HashMap<>();
        for (UrlInfo tempInfo : list) {
            String urlString = tempInfo.url;
            if (dupMap.containsKey(urlString)) {
                UrlInfo existingInfo = (UrlInfo) dupMap.get(urlString)[0];
                int newvalue = existingInfo.termFrequency + tempInfo.termFrequency;
                existingInfo = new UrlInfo(urlString, existingInfo.title, existingInfo.citation, newvalue);
                dupMap.put(urlString, new Object[]{tempInfo, true});
            } else {
                dupMap.put(urlString, new Object[]{tempInfo, false});
            }
        }
        for (String url : dupMap.keySet()) {
            if ((boolean)dupMap.get(url)[1] == true) {
                duplicateList.add((UrlInfo) dupMap.get(url)[0]);
            }
        }
        // Sort final list
        duplicateList.sort((url1, url2) -> url2.termFrequency - url1.termFrequency);
        // create new list
        List<UrlInfo> finalList = new ArrayList<>();
        for (int i = page; page < Math.min(duplicateList.size(), page + 10); i++) {
            finalList.add(duplicateList.get(i));
        }
        // timer stuff
        LocalTime endTime = LocalTime.now(); // end timer
        Duration duration = Duration.between(startTime, endTime);
        double deciseconds = duration.toMillis() / 100;
        averageExecutionTime = (averageExecutionTime + deciseconds) / 2;
        return finalList;
    }

    // Search for URLs containing a given term
    public List<UrlInfo> searchTop10(String[] term) throws RemoteException {
        LocalTime startTime = LocalTime.now(); // start timer
        List<UrlInfo> list = new ArrayList<>();
        for (int i = 1; i < term.length; i++) {
            Map<String, UrlInfo> urlFrequency = invertedIndex.getOrDefault(term[i], Collections.emptyMap());
            List<UrlInfo> urls = new ArrayList<>(urlFrequency.values());
            list.addAll(urls);
        }
        // If there is only one word to search for
        if (term.length == 2) {
            list.sort((url1, url2) -> url2.termFrequency - url1.termFrequency);
            // timer stuff
            LocalTime endTime = LocalTime.now(); // end timer
            Duration duration = Duration.between(startTime, endTime);
            double deciseconds = duration.toMillis() / 100;
            averageExecutionTime = (averageExecutionTime + deciseconds) / 2;
            return list;
        }
        // else, if more than 1 word
        // DO NOT COMBINE the values of duplicate urls -> choose only those the those 2 words
        List<UrlInfo> duplicateList = new ArrayList<>();
        // Object[] { UrlInfo, true or false, true if its duplicate }
        Map<String, Object[]> dupMap = new HashMap<>();
        for (UrlInfo tempInfo : list) {
            String urlString = tempInfo.url;
            if (dupMap.containsKey(urlString)) {
                UrlInfo existingInfo = (UrlInfo) dupMap.get(urlString)[0];
                int newvalue = existingInfo.termFrequency + tempInfo.termFrequency;
                existingInfo = new UrlInfo(urlString, existingInfo.title, existingInfo.citation, newvalue);
                dupMap.put(urlString, new Object[]{tempInfo, true});
            } else {
                dupMap.put(urlString, new Object[]{tempInfo, false});
            }
        }
        for (String url : dupMap.keySet()) {
            if ((boolean)dupMap.get(url)[1] == true) {
                duplicateList.add((UrlInfo) dupMap.get(url)[0]);
            }
        }
        // Sort final list
        duplicateList.sort((url1, url2) -> url2.termFrequency - url1.termFrequency);
        // timer stuff
        LocalTime endTime = LocalTime.now(); // end timer
        Duration duration = Duration.between(startTime, endTime);
        double deciseconds = duration.toMillis() / 100;
        averageExecutionTime = (averageExecutionTime + deciseconds) / 2;
        return duplicateList;
    }
}
