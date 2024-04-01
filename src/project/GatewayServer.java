package project;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import project.interfaces.Barrel_C_I;
import project.interfaces.Gateway_I;
import project.servers.BarrelServer;
import project.servers.ClientServer;
import project.servers.DownloaderServer;

public class GatewayServer extends UnicastRemoteObject implements Gateway_I
{
    // Servers
    static ClientServer clientServer;
    static DownloaderServer downloaderServer;
    static BarrelServer barrelServer;

    // Queue
    LinkedBlockingQueue<String> URL_QUEUE = new LinkedBlockingQueue<String>();

    // Server must know its Barrels
    int num_of_barrels = 0;
    public Barrel_C_I barrel = null;

    public GatewayServer() throws RemoteException {
        super();
    }

    public static void main(String[] args) {
        // Main server creation
        GatewayServer server = null;
        try {
            server = new GatewayServer();
        } catch (RemoteException e) {
            System.out.println("Failure to create server");
            System.exit(0);
        }
        // Client Server
        try {
            clientServer = new ClientServer(server);
            LocateRegistry.createRegistry(1099).rebind("client", clientServer);
            System.out.println("Client Server is ready");
        } catch (RemoteException re) {
			System.out.println("Exception in Client Server: " + re);
            System.exit(0);
		}
        // Downloader Server
        try {
            downloaderServer = new DownloaderServer(server);
            LocateRegistry.createRegistry(1098).rebind("downloader", downloaderServer);
            System.out.println("Downloader Server is ready");
        } catch (RemoteException re) {
			System.out.println("Exception in Downloader Server: " + re);
            System.exit(0);
		}
        // Barrel Server
        try {
            barrelServer = new BarrelServer(server);
            LocateRegistry.createRegistry(1097).rebind("barrel", barrelServer);
            System.out.println("Barrel Server is ready");
        } catch (RemoteException re) {
			System.out.println("Exception in Barrel Server: " + re);
            System.exit(0);
		}
    }

    public void print_on_server(String msg) throws RemoteException {
        System.out.println(msg);
    }

    public void indexUrl(String url) {
        URL_QUEUE.add(url);
    }

    public String removeUrl() {
        try {
            return URL_QUEUE.take();
        } catch (InterruptedException e) {
            return "";
        }
    }

    public void addBarel(Barrel_C_I bar) {
        barrel = bar;
    }

    public String searchWord(String msg) throws RemoteException {
        // for starters lets chekc barrel 0
        return barrel.getUrl(msg);
    }
}
