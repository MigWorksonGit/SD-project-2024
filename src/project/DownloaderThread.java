package project;

import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.StringTokenizer;

import static project.DownloaderMain.*;

class DownloaderThread extends Thread implements Serializable
{
    // multicast stuff here
    // static LinkedList<String> visited = new LinkedList<>();
    // static HashMap<String, HashSet<String>> index = new HashMap<>();
    // private static int recursive = 0;
    // private static int breakpoint = 10;

    private static String MULTICAST_ADDRESS = "230.0.0.1";
    private static int PORT = 4446;

    String name;
    Thread thread;
    DownloaderThread(String name_t) {
        name = name_t;
        thread = new Thread(this, name);
        System.out.println("New downaloder Initialized! " + thread);
        thread.start();
    }

    public void run()
    {
        MulticastSocket multicastSocket = null;
        String url;
        try {
            multicastSocket = new MulticastSocket();
            while (true)
            {
                semaphore.acquire();
                url = URL_QUEUE.remove();
                indexURL(url, multicastSocket);
                // System.out.println("Downloader " + thread + " obtained URL: " + url);
            }
        }
        catch (InterruptedException e) {
            System.out.println("Thread " + thread + " was interrupted!");
        } catch (IOException e) {
            System.out.println("Error while creating multicast");
        } finally {
            multicastSocket.close();
        }
    }

    void indexURL(String url, MulticastSocket multicastSocket)
    {
        LinkedList<String> visited = new LinkedList<>();
        HashMap<String, HashSet<String>> index = new HashMap<>();   // NOT SURE IF ITS HERE!!!
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

            Url url_new = new Url(url, doc.title(), citation);

            // Convert url_new to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(url_new);
            oos.flush();
            byte[] data = baos.toByteArray();

            InetAddress multicastAddress = InetAddress.getByName(MULTICAST_ADDRESS);

            // Send the byte array via multicast
            DatagramPacket packet = new DatagramPacket(data, data.length, multicastAddress, PORT);
            multicastSocket.send(packet);

            Elements links = doc.select("a[href]");

            for (Element link : links) {
                if(index.containsKey( link.attr("abs:href"))){
                    if(!index.get(link.attr("abs:href")).contains(url))
                        index.get(link.attr("abs:href")).add(url);
                }
                else{
                    HashSet<String> aux = new HashSet<String>();
                    aux.add(url);
                    index.put(link.attr("abs:href"),aux);
                }
                URL_QUEUE.add(link.attr("abs:href"));
                semaphore.release();                                // Increasing Sempahore here
                visited.add(link.attr(url));
            }
        }
        catch (IllegalArgumentException | IOException e) {
            visited.add(url);
            url = URL_QUEUE.remove();
            while (check_visited(url, visited)) {
                url = URL_QUEUE.remove();
            }
            //recursive++;
        }
        multicastSocket.close();
    }

    boolean check_visited(String url, LinkedList<String> visited) {
        for (String str : visited)
            return Objects.equals(str, url);
        return false;
    }
}
