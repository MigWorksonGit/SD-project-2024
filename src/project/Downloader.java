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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import project.interfaces.Downloader_I;
import project.resources.Message;
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
                    try {
                        process_url(element.url, element.recursion_level, multicastSocket, server);
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

    static String removerPontuação(String text) {
        if (text == null) return "";
        String temp = text.replaceAll("[.,;:\\\"'?!«»()\\[\\]{}-]", "");
        return temp;
    }

    static void process_url(String url, int recursive, MulticastSocket multicastSocket, Downloader_I server)
    throws IOException
    {
        if (url.equals("")) return;
        if (!url.startsWith("https://")) return;
        if (recursive == 0) return;

        HashSet<String> visited_words = new HashSet<>();
        try {
            Document doc = Jsoup.connect(url).get();
            StringTokenizer tokens = new StringTokenizer(doc.text());

            String word;
            WebPage newpage = new WebPage(url, doc.title(), "no result");
            while (tokens.hasMoreElements())
            {
                word = removerPontuação(tokens.nextToken().strip().toLowerCase());
                if (!word.matches("[a-zA-Z].*")) {
                    continue;
                }
                if (visited_words.contains(word)) {
                    continue;
                }

                newpage.citation = "no result";
                // get paragraph with word
                Element pContainsWord = null;
                for (Element p : doc.select("p")) {
                    if (p.text().toLowerCase().contains(word)) {
                        pContainsWord = p;
                        break;
                    }
                }
                if (pContainsWord != null) {
                    String citation = pContainsWord.text();
                    // Split the paragraph text into words
                    List<String> temp = Arrays.asList(citation.split("\\s+"));
                    int idx = -1;
                    for (int i = 0; i < temp.size(); i++) {
                        if (temp.get(i).toLowerCase().contains(word)) {
                            idx = i;
                            break;
                        }
                    }
                    // Extract 10 words centered around the target word
                    StringBuilder result = new StringBuilder();
                    if (idx != -1) {
                        int startIndex = Math.max(0, idx - 5);
                        int endIndex = Math.min(temp.size(), idx + 6);
                        for (int i = startIndex; i < endIndex; i++) {
                            result.append(temp.get(i)).append(" ");
                        }
                    }
                    newpage.citation = result.toString().trim();
                } else {
                    Element pFirst = doc.select("p").first();
                    if (pFirst != null) {
                        String[] paragraph = pFirst.text().split("\\s+");
                        StringBuilder limitedText = new StringBuilder();
                        for (int i = 0; i < Math.min(paragraph.length, 10); i++) {
                            limitedText.append(paragraph[i]).append(" ");
                        }
                        newpage.citation = limitedText.toString().toLowerCase();
                    }
                }

                Message message2send = new Message(word, newpage);
            
                // These things here trow a lot of IO exceptions
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(message2send);
                oos.flush();
                byte[] data = baos.toByteArray();

                InetAddress multicastAddress = InetAddress.getByName(MULTICAST_ADDRESS);

                // Send the byte array via multicast
                DatagramPacket packet = new DatagramPacket(data, data.length, multicastAddress, MULTICAST_PORT);
                multicastSocket.send(packet);
                
                visited_words.add(word);
            }
            // then obtain the links and do magic
            Elements links = doc.select("a[href]");
            String newUrl;
            for (Element link : links) {
                newUrl = link.attr("abs:href");
                server.indexUrl2(new UrlQueueElement(newUrl, recursive-1));
            }
        } catch (IOException e) {
            System.out.println("IO exception found in process_url" + e);
            throw new IOException();
        }
    }
}
