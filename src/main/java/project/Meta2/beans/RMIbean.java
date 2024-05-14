package project.Meta2.beans;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import project.Meta1.beans.UrlQueueElement;
import project.Meta1.interfaces.Client_I;
import project.config.ConfigFile;

public class RMIbean {
    private Client_I server = null;

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    int DEBUG_recursion_level = 2;

    public void connectToRMIserver() {
        // Connecting to RMI server
        ConfigFile data = new ConfigFile();
        data.getJsonInfo();
        String lookup = "rmi://" + data.getIp() + ":" + data.getPort() + "/client";
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
    }

    public void indexUrl(String url, String fatherUrl) {
        if (url.equalsIgnoreCase("-1")) {
            System.out.println("Not good");
            return;
        }
        try {
            server.indexUrl(new UrlQueueElement(url, DEBUG_recursion_level, "-1"));
        } catch (Exception e) {
            System.out.println("Cant connect to server");
        }
    }

    public void printHelloWorld() {
        try {
            server.print_on_server("Hello World!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
