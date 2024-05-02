package project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import project.beans.UrlInfo;
import project.beans.UrlQueueElement;
import project.interfaces.Client_I;

public class Client
{
    static int DEBUG_recursion_level = 2;
    
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
                    if (words[0].equals("search"))
                    {
                        try {
                            System.out.print("Urls containing the word(s): ");
                            for (int i = 1; i < words.length; i++) {
                                System.out.print(words[i] + " ");
                            }
                            System.out.println("");
                            int counter = 0;
                            loop:
                            do {
                                // Error here!! when choosing 2 and both exist
                                List<UrlInfo> top10 = server.searchTop10_BarrelPartition(words, counter);
                                if (top10.size() == 0) {
                                    break loop;
                                }
                                for (int i = 0; i < top10.size(); i++) {
                                    System.out.println("-----");
                                    System.out.println(top10.get(i));
                                }
                                if (top10.size() < 10) break loop;
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
                        } catch (Exception e) {
                            System.out.println("Smth here");
                        }
                    }
                    else
                    if (words[0].equals("consult"))
                    {
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
                    if (words[0].equals("admin") && words[1].equals("info"))
                    {
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
