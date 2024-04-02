package project;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

import project.interfaces.Client_I;
import project.resources.UrlQueueElement;

public class Client
{
    static int DEBUG_recursion_level = 1;

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
                                server.indexUrl(new UrlQueueElement(words[1], DEBUG_recursion_level, null));
                                // server.print_on_server(words[1]);
                            } catch (MalformedURLException e) {
                                System.out.println("Url inserido inválido");
                            }
                        }
                    }
                    else
                    if (words[0].equals("search")) {
                        String msg = server.searchWord(words[1]);
                        // if (msg.equals("")) System.out.println("Not found");
                        // else
                        System.out.println(msg);
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
