package project;

import java.net.MalformedURLException;
//import java.net.URL;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

import project.interfaces.Gateway_I;

public class Client
{
    public static void main(String[] args) {
        try {
            Gateway_I server = null;
            //Client client = new Client();
            // See this more.
            // Try and give correct error messages and such
            try {
                try {
                    server = (Gateway_I) Naming.lookup("rmi://localhost:1099/hello");
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
                                server = (Gateway_I) Naming.lookup("rmi://localhost:1099/hello");
                                //new URL(words[1]);
                                server.receive_url(words[1]);
                            } catch (MalformedURLException e) {
                                System.out.println("Url inserido inválido");
                            }
                        }
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
