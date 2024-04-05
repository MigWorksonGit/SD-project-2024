package project;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

import project.interfaces.Barrel_C_I;
import project.interfaces.Gateway_I;
import project.resources.UrlInfo;
import project.resources.UrlQueueElement;
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
        return new UrlQueueElement("failed to obtain url", 0, "-1");
    }

    public void addBarel(Barrel_C_I bar) throws RemoteException {
        // This is kinda slow due to catching exception but works as intended
        if (num_of_barrels == 0) {
            barrels.add(bar);
            barrels.get(0).setName("barrel_0");
            num_of_barrels++;
            return;
        }
        for (int i = 0; i < num_of_barrels; i++) {
            try {
                barrels.get(i).isAlive();
            } catch (IndexOutOfBoundsException e) {
                System.out.println("This is out of bounds " + i);
                return;
            } catch (RemoteException e) {
                System.out.println("Barrel " + i + " no longer exists");
                barrels.set(i, bar);
                barrels.get(i).setName("barrel_" + i);
                return;
            }
        }
        // if they are all alive
        barrels.add(bar);
        barrels.get(num_of_barrels).setName("barrel_" + num_of_barrels);
        num_of_barrels++;
    }

    public List<String> getUrlsConnected2this(String msg) throws RemoteException {
        // for starters lets chekc barrel 0
        return barrels.get(0).getUrlsConnected2this(msg);
    }

    // If remote exception is received, it means the barrel on the current index
    // has been deactivated
    public List<UrlInfo> searchTop10(String[] term) throws RemoteException {
        try {
            return barrels.get(0).searchTop10(term);
        } catch (RemoteException e) {
            System.out.println("Hahaha remote exception " + e);
            return null;
        }
    }

    // If index 0 is out of bounds: No existing barrels
    // If RemoteException is received
    public String getAdminInfo() throws RemoteException {
        StringBuilder info = new StringBuilder();

        return info.toString().toLowerCase();
    }
}
