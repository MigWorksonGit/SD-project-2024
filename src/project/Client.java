package project;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Scanner;

import project.interfaces.Client_I;
import project.resources.UrlInfo;
import project.resources.UrlQueueElement;

public class Client
{
    static int DEBUG_recursion_level = 2;

    public static void main(String[] args) {
        try {
            Client_I server = null;
            // Try and give correct error messages and such
            try {
                try {
                    server = (Client_I) Naming.lookup("rmi://localhost:1099/client");
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
                System.out.println("Client sent subscription to server");
            } catch (RemoteException e) {
                System.out.println("Error comunicating to the server");
                System.exit(0);
            }

            // Obtain Input
            try (Scanner sc = new Scanner(System.in)) {
                String input;
                String[] words;
        
                while (true) {
                    System.out.println("Enter a command:");
                    input = sc.nextLine();
                    words = input.trim().split(" ");
        
                    if (words[0].equals("index")) {
                        // if no url is given it just dies.
                        if (!words[1].startsWith("https"))
                            System.out.println("Url must start with https");
                        else {
                            try {
                                server = (Client_I) Naming.lookup("rmi://localhost:1099/client");
                                server.indexUrl(new UrlQueueElement(words[1], DEBUG_recursion_level, "-1"));
                            } catch (MalformedURLException e) {
                                System.out.println("Url inserido inválido");
                            }
                        }
                    }
                    else
                    if (words[0].equals("search")) {
                        List<UrlInfo> top10 = server.searchTop10(words);
                        System.out.println("URLs containing the term '" + words[1] + "', prioritized by frequency:");
                        int counter = 0;
                        loop:
                        do {
                            for (int i = counter; i < counter+10; i++) {
                                if (i == top10.size()) break loop;
                                System.out.println(top10.get(i));
                            }
                            System.out.println("Input \"next\" for next set of pages, \"end\" to stop");
                            input = sc.nextLine();
                            if (input.equals("end")) break;
                            else if (input.equals("next")) {
                                counter += 10;
                            }
                            else {
                                System.out.println("Wrong input, backing out...");
                                break;
                            }
                        } while (true);
                    }
                    else
                    if (words[0].equals("consult")) {
                        List<String> list = server.getUrlsConnected2this(words[1]);
                        for (String url : list) {
                            System.out.println(url);
                        }
                    }
                    else
                    if (words[0].equals("admin")) {
                        // Show admin page
                        
                    }
                    else
                    if (words[0].equals("exit")) {
                        return;
                    }
                }
            }
            catch (Exception e) {
                System.out.println("Error found in scanner " + e);
            }
        }
        catch (Exception e) {
            System.out.println("Exception in main: " + e);
        }
    }
}
