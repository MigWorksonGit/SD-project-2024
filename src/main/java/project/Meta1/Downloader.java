package project.Meta1;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
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

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import project.Meta1.beans.Message;
import project.Meta1.beans.UrlQueueElement;
import project.Meta1.beans.WebPage;
import project.Meta1.interfaces.Downloader_I;

public class Downloader
{
    private static String MULTICAST_ADDRESS = "230.0.0.1";
    private static int MULTICAST_PORT = 4446;
    
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
        String lookup = "rmi://" + IP + ":" + PORT + "/downloader";
        try {
            Downloader_I server = null;
            try {
                try {
                    server = (Downloader_I) Naming.lookup(lookup);
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
                    element = server.removeUrl();
                    try {
                        process_url(element.getUrl(), element.getFatherUrl(), element.getRecursionLvel(), multicastSocket, server);
                    } catch (SocketTimeoutException e) {
                        // Add element back to queue
                        server.indexUrl(element);
                    } catch (IOException e) {
                        continue;
                    }
                }
            } catch (IOException e) {
                System.out.println("Error while creating multicast or server died");
            }
            catch (NullPointerException e) {
                System.out.println("Something thows this" + e);
            }
            finally {
                multicastSocket.close();
            }
        }
        catch (Exception e) {
            System.out.println("Exception in main: " + e);
        }
    }

    static String removerPontuacao(String text) {
        if (text == null) return "";
        String temp = text.replaceAll("[.,;:\\\"'?!|«»()\\[\\]{}-]", "");
        return temp;
    }

    static void process_url(String url, String father_url, int recursive, MulticastSocket multicastSocket, Downloader_I server)
    throws IOException, SocketTimeoutException
    {
        if (url.equals("")) return;
        if (!url.startsWith("https://")) return;
        if (recursive == 0) return;

        HashSet<String> visited_words = new HashSet<>();
        try {
            Document doc = Jsoup.connect(url).get();
            StringTokenizer tokens = new StringTokenizer(doc.text());

            String word;
            WebPage newpage = new WebPage(url, doc.title(), "no_result", father_url);
            while (tokens.hasMoreElements())
            {
                word = removerPontuacao(tokens.nextToken().strip().toLowerCase());
                if (!word.matches("[a-zA-Z].*")) {
                    continue;
                }
                if (visited_words.contains(word)) {
                    continue;
                }

                newpage.setCitation("no_result");
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
                    newpage.setCitation(result.toString().trim());
                } else {
                    Element pFirst = doc.select("p").first();
                    if (pFirst != null) {
                        String[] paragraph = pFirst.text().split("\\s+");
                        StringBuilder limitedText = new StringBuilder();
                        for (int i = 0; i < Math.min(paragraph.length, 10); i++) {
                            limitedText.append(paragraph[i]).append(" ");
                        }
                        newpage.setCitation(limitedText.toString().toLowerCase());
                    }
                }

                Message message2send = new Message(word, newpage);
            
                // These things here trow a lot of IO exceptions
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(message2send);
                oos.flush();
                byte[] data = baos.toByteArray();

                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);

                // Send the byte array via multicast
                DatagramPacket packet = new DatagramPacket(data, data.length, group, MULTICAST_PORT);
                multicastSocket.send(packet);

                // Receive acknowledgement. If not received, send again -> do this
                byte[] buf = new byte[124];
                DatagramPacket packetReceiver = new DatagramPacket(buf, buf.length, group, MULTICAST_PORT);

                multicastSocket.setSoTimeout(5000);
                multicastSocket.receive(packetReceiver);
                System.out.println("Processed word: " + word);

                visited_words.add(word);
            }
            // then obtain the links and do magic
            Elements links = doc.select("a[href]");
            String newUrl;
            for (Element link : links) {
                newUrl = link.attr("abs:href");
                server.indexUrl(new UrlQueueElement(newUrl, recursive-1, url));
            }
        } 
        catch (SocketTimeoutException e) {
            System.out.println("Socket timeout found in process_url " + e);
            throw new SocketTimeoutException();
        }
        catch (IOException e) {
            System.out.println("IO exception found in process_url " + e);
            throw new IOException();
        }
    }
}
