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
    static int DEBUG_recursion_level = 10;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Wrong number of arguments. Please insert Ip and Port");
            System.exit(0);
        }
        String IP = args[0];
        String PORT = args[1];
        if (!validIpv4(IP)) {
            System.out.println("Not a valid IP address");
            System.exit(0);
        }
        if (!validPort(PORT)) {
            System.out.println("Number is not an Integer");
            System.exit(0);
        }
        String lookup = "rmi://" + IP + ":" + PORT + "/client";
        try {
            Client_I server = null;
            try {
                try {
                    server = (Client_I) Naming.lookup(lookup);
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
                            // Also here dont forget
                            server = (Client_I) Naming.lookup(lookup);
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
                        // try {
                        //     //List<UrlInfo> top10 = server.searchTop10(words);
                        //     // System.out.println("URLs containing the term '" + words[1] + "', prioritized by frequency:");
                        //     System.out.print("Urls containing the word(s): ");
                        //     for (int i = 1; i < words.length; i++) {
                        //         System.out.print(words[i] + " ");
                        //     }
                        //     System.out.println("");
                        //     int counter = 0;
                        //     loop:
                        //     do {
                        //         for (int i = counter; i < counter+10; i++) {
                        //             if (i == top10.size()) {
                        //                 System.out.println("-----");
                        //                 break loop;
                        //             }
                        //             System.out.println("-----");
                        //             System.out.println(top10.get(i));
                        //         }
                        //         System.out.println("Input \"next\" for next set of pages, \"end\" to stop");
                        //         input = sc.nextLine();
                        //         if (input.equals("end")) break;
                        //         else if (input.equals("next")) {
                        //             counter += 10;
                        //         }
                        //         else {
                        //             System.out.println("Wrong input, backing out...");
                        //             break;
                        //         }
                        //     } while (true);
                        // } catch (RemoteException e) {
                        //     System.out.println("Cant connect to barrels");
                        // }
                        try {
                            System.out.print("Urls containing the word(s): ");
                            for (int i = 1; i < words.length; i++) {
                                System.out.print(words[i] + " ");
                            }
                            System.out.println("");
                            int counter = 0;
                            loop:
                            do {
                                List<UrlInfo> top10 = server.searchTop10_BarrelPartition(words, counter);
                                for (int i = 0; i < top10.size(); i++) {
                                    // Does not work correctly. Please fix.
                                    if (top10.size() == 0) {
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

    public static boolean validIpv4(String ip) {
        try {
            if (ip.equals("localhost")) return true;
            String[] parts = ip.split("\\.");
            if (parts.length != 4) return false;
            for (String s : parts) {
                int i = Integer.parseInt(s);
                if (i<0 || i > 255) return false;
            }
            if (ip.endsWith(".")) return false;
            return true;
        } catch (NumberFormatException e) { return false; }
    }

    public static boolean validPort(String port) {
        try {
            Integer.parseInt(port);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
