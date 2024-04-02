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
                    //DEBUG_testMulticast(element.url, multicastSocket);
                    //DEBUG_testMulticast_element(element, multicastSocket);
                    try {
                        process_url(element.url, element.recursion_level, element.fatherPage, multicastSocket, server);
                    } catch (IOException e) {
                        continue;
                    }
                    //System.out.println("Downloader obtained URL: " + element.url);
                }
            } catch (IOException e) {
                System.out.println("Error while creating multicast or server died");
            }
            catch (NullPointerException e) {
                System.out.println("Something thows this" + e);
                // Idk what it is but try and recover from it
            }
            finally {
                multicastSocket.close();
            }
        }
        // Something causes this to happen and idk what it is
        // Its because of the url given
        // One error is:
        // Exception in main: java.lang.IllegalArgumentException: The supplied URL, 'steam://store/2059170', is malformed. 
        // Make sure it is an absolute URL, and starts with 'http://' or 'https://'. See https://jsoup.org/cookbook/extracting-data/working-with-urls
        // So gotta fix that
        // The worst error is NULL exception
        catch (Exception e) {
            System.out.println("Exception in main: " + e);
        }
    }

    static void DEBUG_testMulticast_string(String url, MulticastSocket multicastSocket) {
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

    static void DEBUG_testMulticast_element(UrlQueueElement element, MulticastSocket multicastSocket) {
        try {
            WebPage pageObj = new WebPage("hello world!", element.url, "Page title <3", "citation");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(pageObj);
            oos.flush();
            byte[] data = baos.toByteArray();

            InetAddress multicastAddress = InetAddress.getByName(MULTICAST_ADDRESS);

            // Send the byte array via multicast
            DatagramPacket packet = new DatagramPacket(data, data.length, multicastAddress, MULTICAST_PORT);
            multicastSocket.send(packet);
        } catch (IOException e) {
            System.out.println("Error while reading stream header");
        }
    }

    static String removerPontuação(String text) {
        if (text == null) return "";
        String temp = text.replaceAll("[.,;:\\\"'?!«»()\\[\\]{}-]", "");
        return temp;
    }

    static void process_url(String url, int recursive, WebPage fatherPage, MulticastSocket multicastSocket, Downloader_I server)
    throws IOException
    {
        // This should probably fix all problems
        // It does not
        if (url.equals("")) {
            return;
        }
        if (!url.startsWith("http")) {
            return;
        }
        // Instead of asking the server ,why not ask the barrels?
        if (fatherPage != null) {
            try {
                if (server.containsUrl(url)) {
                    //System.out.println("Server (fp not null) contais url: " +  url);
                    server.addPage2Url(url, fatherPage);
                    return;
                } else {
                    HashSet<WebPage> temp_hash = new HashSet<>();
                    temp_hash.add(fatherPage);
                    server.putUrl(url, temp_hash);
                }
            } catch (RemoteException e) {
                System.out.println("Error processing url when FatherPage not null");
                return;
            }
        } else {
            try {
                if (server.containsUrl(url)) {
                    //System.out.println("Server (fp is null) contais url: " +  url);
                    return;
                }
                else {
                    HashSet<WebPage> temp_hash = new HashSet<>();
                    server.putUrl(url, temp_hash);
                }
            } catch (RemoteException e) {
                System.out.println("Error processing url when FatherPage is null");
                return;
            }
        }
        if (recursive == 0) {
            //System.out.println("Recursion finished");
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
                //System.out.println(word);

                WebPage pageObj = new WebPage(word, newpage.url, newpage.title, newpage.citation);
            
                // These things here trow a lot of IO exceptions
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(pageObj);
                oos.flush();
                byte[] data = baos.toByteArray();

                InetAddress multicastAddress = InetAddress.getByName(MULTICAST_ADDRESS);

                // Send the byte array via multicast
                DatagramPacket packet = new DatagramPacket(data, data.length, multicastAddress, MULTICAST_PORT);
                multicastSocket.send(packet);

                // Check for acknowledgement here!

                visited_words.add(word);
            }

            Elements links = doc.select("a[href]");
            String newUrl;
            for (Element link : links) {
                newUrl = link.attr("abs:href");
                if (server.containsUrl(newUrl)) {
                    server.addPage2Url(newUrl, newpage);
                } else {
                    server.indexUrl2(new UrlQueueElement(newUrl, recursive-1, newpage));
                }
            }
        } catch (IOException e) {
            System.out.println("IO exception found in process_url" + e);
            throw new IOException();
        }
    }
}
