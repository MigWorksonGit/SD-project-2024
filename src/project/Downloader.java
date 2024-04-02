package project;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.MulticastSocket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import project.interfaces.Downloader_I;
import project.resources.UrlQueueElement;
import project.resources.WebPage;

public class Downloader
{
    private static String MULTICAST_ADDRESS = "230.0.0.1";
    private static int MULTICAST_PORT = 4446;

    // links visited by this downloader. Maybe put it somewhere else? Like, on the server?
    // Is this really needed tho?
    static HashMap<String, HashSet<WebPage>> visited_links = new HashMap<>();

    public static void main(String[] args) {
        try {
            Downloader_I server = null;
            // Try and give correct error messages and such

            try {
                try {
                    server = (Downloader_I) Naming.lookup("rmi://localhost:1098/downloader");
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
                System.out.println("Downloader is ready");
            } catch (RemoteException e) {
                System.out.println("Error comunicating to the server");
                System.exit(0);
            }

            MulticastSocket multicastSocket = null;
            UrlQueueElement element;
            // Do your stuff
            try {
                multicastSocket = new MulticastSocket();
                while (true)
                {
                    element = server.removeUrl2();
                    DEBUG_testMulticast(element.url, multicastSocket);
                    //process_url(element.url, element.recursion_level, element.fatherPage, multicastSocket, server);
                    System.out.println("Downloader obtained URL: " + element.url);
                }
            } catch (IOException e) {
                System.out.println("Error while creating multicast");
            } 
            finally {
                multicastSocket.close();
            }
        }
        catch (Exception e) {
            System.out.println("Exception in main: " + e);
        }
    }

    static void DEBUG_testMulticast(String url, MulticastSocket multicastSocket) {
        try {
            byte[] data = url.getBytes();
            InetAddress multicastAddress = InetAddress.getByName(MULTICAST_ADDRESS);
            // Send the byte array via multicast
            DatagramPacket packet = new DatagramPacket(data, data.length, multicastAddress, MULTICAST_PORT);
            multicastSocket.send(packet);
        }
        catch (IOException e) {
            System.out.println("Error while reading stream header");
        }
    }

    static String removerPontuação(String text) {
        if (text == null) return "";
        String temp = text.replaceAll("[.,;:\\\"'?!«»()\\[\\]{}-]", "");
        return temp;
    }

    static void process_url(String url, int recursive, WebPage fatherPage, MulticastSocket multicastSocket, Downloader_I server)
    {
        if (fatherPage != null) {
            ;
        }
        if (recursive == 0) {
            System.out.println("Recursion finished");
            return;
        }
        HashSet<String> visited_words = new HashSet<>();
        try {
            Document doc = Jsoup.connect(url).get();
            StringTokenizer tokens = new StringTokenizer(doc.text());
            String citation = "";

            String word;
            WebPage newpage = new WebPage("", url, doc.title(), citation);
            // Iterate trought all words of the link
            while (tokens.hasMoreElements())
            {
                word = removerPontuação(tokens.nextToken().strip().toLowerCase());
                if (!word.matches("[a-zA-Z].*")) {
                    continue;
                }
                if (visited_words.contains(word)) {
                    continue;
                }
                System.out.println(word);

                // WebPage pageObj = new WebPage(word, newpage.url, newpage.title, newpage.citation);
            
                // ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // ObjectOutputStream oos = new ObjectOutputStream(baos);
                // oos.writeObject(pageObj);
                // oos.flush();
                // byte[] data = baos.toByteArray();

                // InetAddress multicastAddress = InetAddress.getByName(MULTICAST_ADDRESS);

                // // Send the byte array via multicast
                // DatagramPacket packet = new DatagramPacket(data, data.length, multicastAddress, MULTICAST_PORT);
                // multicastSocket.send(packet);

                // Check for acknowledgement here!

                visited_words.add(word);
            }

            Elements links = doc.select("a[href]");
            String newUrl;
            for (Element link : links) {
                newUrl = link.attr("abs:href");
                System.out.println(newUrl);
                // check if visited links contains the new url
                // if not then add into the queue and increase mutex
                // maybe create a QueueElement class and not just Strings
                // -> With url, recursive depth, fatherPage
                server.indexUrl2(new UrlQueueElement(newUrl, recursive-1, newpage));
            }


        } catch (IOException e) {
            System.out.println("IO exception found in process_url");
            return;
        }
    }
}
