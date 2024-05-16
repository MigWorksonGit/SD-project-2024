package project.Meta2.beans;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

import project.Meta1.beans.UrlInfo;
import project.Meta1.beans.UrlQueueElement;
import project.Meta1.interfaces.Client_I;
import project.config.ConfigFile;

public class RMIbean {
    private Client_I server = null;

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    int DEBUG_recursion_level = 10;

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

    public List<String> getUrlsConnected2this(String words) {
        try {
            return server.getUrlsConnected2this(words);
        } catch (Exception e) {
            System.out.println("Kek");
        }
        return null;
    }

    public List<UrlInfo> searchTop10_barrelPartition(String[] term, int page){
        try {
            String temp = "pog ";
            for (int i = 0; i < term.length; i++) {
                temp += term[i];
            }
            String[] input = temp.trim().split(" ");
            return server.searchTop10_BarrelPartition(input, page);
        } catch (RemoteException e ) {
            System.out.println("kek");
        }
        return null;
    }

    public String getAdminInfo() {
        try {
            return server.getAdminInfo();
        } catch (RemoteException e) {
            return "skill issue";
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
