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

            try (Scanner sc = new Scanner(System.in)) {
                String input;
                String[] words;
        
                while (true) {
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
                    } catch (RemoteException e) {
                        System.out.println("Error comunicating to the server");
                        System.exit(0);
                    }
                    System.out.println("Enter a command:");
                    input = sc.nextLine();
                    words = input.trim().split(" ");

                    if (words.length < 2) {
                        System.out.println("Incorrect input");
                    }
                    else
                    if (words[0].equals("index"))
                    {
                        if (!words[1].startsWith("https"))
                            System.out.println("Url must start with https");
                        else {
                            try {
                                server.indexUrl(new UrlQueueElement(words[1], DEBUG_recursion_level, "-1"));
                            } catch (Exception e) {
                                System.out.println("Cant connect to server");
                            }
                        }
                    }
                    else
                    if (words[0].equals("search")) {
                        try {
                            List<UrlInfo> top10 = server.searchTop10(words);
                            // System.out.println("URLs containing the term '" + words[1] + "', prioritized by frequency:");
                            System.out.print("Urls containing the word(s): ");
                            for (String wString : words) {
                                System.out.print(wString + " ");
                            }
                            System.out.println("");
                            int counter = 0;
                            loop:
                            do {
                                for (int i = counter; i < counter+10; i++) {
                                    if (i == top10.size()) {
                                        System.out.println("-----");
                                        break loop;
                                    }
                                    System.out.println("-----");
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
                        } catch (RemoteException e) {
                            System.out.println("Cant connect to barrels");
                        }
                    }
                    else
                    if (words[0].equals("consult")) {
                        try {
                            List<String> list = server.getUrlsConnected2this(words[1]);
                            for (String url : list) {
                                if (!url.equals("-1")) System.out.println(url);
                            }
                        } catch (RemoteException e) {
                            System.out.println("Cant connect to barrels");
                        }
                    }
                    else
                    if (words[0].equals("admin") && words[1].equals("info")) {
                        // Show admin page
                        String adminInfo = server.getAdminInfo();
                        System.out.println(adminInfo);
                    }
                    else
                    if (words[0].equals("exit") && words[1].equals("client")) {
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
