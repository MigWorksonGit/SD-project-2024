package project;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class GatewayServer extends UnicastRemoteObject implements Gateway_I
{
    static HashMap<String, Client> clients;
    static Downloader_I downloaderInterface;

    public GatewayServer() throws RemoteException {
        super();
    }

    public static void main(String[] args) {
        clients = new HashMap<String, Client>();

        try {
            GatewayServer server = new GatewayServer();
            LocateRegistry.createRegistry(1099).rebind("hello", server);
            System.out.println("Server is ready");
        }
        catch (RemoteException re) {
			System.out.println("Exception in GatewayServer.main: " + re);
		}

        downloaderInterface = null;
        try {
            try {
                downloaderInterface = (Downloader_I) Naming.lookup("rmi://localhost:1098/downloaders");
            }
            catch(MalformedURLException e) {
                System.out.println("Server Url is incorrectly formed");
                System.out.println("Downloader Cant comunicate with server...");
                System.out.println("Closing...");
                System.exit(0);
            }
            catch (NotBoundException e) {
                System.out.println("Downaloder Cant comunicate with server...");
                System.out.println("Closing...");
                System.exit(0);
            }
        } catch (RemoteException e) {
            System.out.println("Downaloder Error comunicating to the server");
            System.exit(0);
        }
    }

    public void print_on_server(String msg) throws RemoteException {
        System.out.println(msg);
    }

    public void receive_url(String url) throws RemoteException {
        try {
            try {
                downloaderInterface = (Downloader_I) Naming.lookup("rmi://localhost:1098/downloaders");
                downloaderInterface.sendUrlToDownloaders(url);
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
    }

}
