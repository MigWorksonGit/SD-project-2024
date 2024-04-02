package project;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

import project.interfaces.Barrel_C_I;
import project.interfaces.Gateway_I;
import project.resources.UrlQueueElement;
import project.resources.WebPage;
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
    Semaphore mutex = new Semaphore(0);
    ConcurrentLinkedQueue<UrlQueueElement> URL_QUEUE = new ConcurrentLinkedQueue<>();

    // Server must know its Barrels
    int num_of_barrels = 0;
    private ArrayList<Barrel_C_I> barrels = new ArrayList<>();

    // links visited by this downloader.
    public HashMap<String, HashSet<WebPage>> visited_links = new HashMap<>();

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

    public void indexUrl(UrlQueueElement element) {
        URL_QUEUE.add(element);
        mutex.release();
    }

    public UrlQueueElement removeUrl() {
        try {
            while (true) {
                mutex.acquire();
                //System.out.println("DEBUB semaphore working");
                return URL_QUEUE.remove();
            }
        } catch (InterruptedException e) {
            System.out.println("Thread was interrupted");
        }
        return new UrlQueueElement("failed to obtain url", 0, null);
    }

    public void addBarel(Barrel_C_I bar) {
        barrels.add(bar);
    }

    public String searchWord(String msg) throws RemoteException {
        // for starters lets chekc barrel 0
        return barrels.get(0).getUrl(msg);
    }

    public boolean containsUrl(String url) {
        return visited_links.containsKey(url);
    }

    public void addPage2Url(String url, WebPage fatherPage) {
        visited_links.get(url).add(fatherPage);
    }

    public void putUrl(String url, HashSet<WebPage> temp_hash) {
        visited_links.put(url, temp_hash);
    }
}
