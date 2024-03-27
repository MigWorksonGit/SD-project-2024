import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.concurrent.LinkedBlockingQueue;

public class OLDDownloader {

    static LinkedList<String> visited = new LinkedList<>();
    static LinkedBlockingQueue<String> Url_Queue = new LinkedBlockingQueue<>();
    static HashMap<String, HashSet<String>> index = new HashMap<>();
    private static int recursive = 0;
    private static int breakpoint = 10;
    private static String url;

    private static String MULTICAST_ADDRESS = "230.0.0.1";
    private static int PORT = 4446;

    public OLDDownloader() {}

    public static void main(String args[]) throws InterruptedException {
        InetAddress multicastAddress;

        try {
            multicastAddress = InetAddress.getByName(MULTICAST_ADDRESS);
            MulticastSocket multicastSocket = new MulticastSocket();

            while (recursive <= breakpoint) {
                url = Url_Queue.take();
                try {
                    Document doc = Jsoup.connect(url).get();
                    StringTokenizer tokens = new StringTokenizer(doc.text());
                    int countTokens = 0;
                    String citation = "";
                    while (tokens.hasMoreElements() && countTokens++ < 100) {
                        Elements paragraphs = doc.select("p");

                        try {
                            citation = Objects.requireNonNull(paragraphs.first()).text();
                        } catch (NullPointerException e) {
                            citation = "None";
                        }
                        //System.out.println(tokens.nextToken().toLowerCase());
                    }

                    OLDUrl url_new = new OLDUrl(url, doc.title(), citation);

                    // Convert url_new to byte array
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(url_new);
                    oos.flush();
                    byte[] data = baos.toByteArray();

                    // Send the byte array via multicast
                    DatagramPacket packet = new DatagramPacket(data, data.length, multicastAddress, PORT);
                    multicastSocket.send(packet);

                    Elements links = doc.select("a[href]");
                    //for (Element link : links)
                    //  System.out.println(link.text() + "\n" + link.attr("abs:href") + "\n");
                    
                    for (Element link : links) {
                        if(index.containsKey( link.attr("abs:href"))){
                            if(!index.get(link.attr("abs:href")).contains(url))
                                index.get(link.attr("abs:href")).add(url);
                        }
                        else{
                            HashSet aux = new HashSet();
                            aux.add(url);
                            index.put(link.attr("abs:href"),aux);
                        }
                        Url_Queue.add(link.attr("abs:href"));
                        visited.add(link.attr(url));
                    }
                } catch (IllegalArgumentException | IOException e) {
                    visited.add(url);
                    url = Url_Queue.take();
                    while (check_visited(url)) {
                        url = Url_Queue.take();
                    }
                    recursive++;
                }
                multicastSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean check_visited(String url) {
        for (String str : visited)
            return Objects.equals(str, url);
        return false;
    }
    
}

