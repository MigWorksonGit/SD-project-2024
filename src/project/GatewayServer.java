package project;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Comparator;
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
    private ArrayList<Integer> active_barrel_idx = new ArrayList<>();
    private ArrayList<Barrel_C_I> barrels = new ArrayList<>();
    // Thread safe list.
    // Object[0] is the word, Object[1] is the number of times that word was searched
    Semaphore list_mutex = new Semaphore(1);
    private ArrayList<Object[]> threadSafeList = new ArrayList<>();

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

    public void removeBarrel(int index) throws RemoteException {
        for (int i = 0; i < active_barrel_idx.size(); i++) {
            if (active_barrel_idx.get(i) == index) {
                active_barrel_idx.remove(i);
            }
        }
    }

    public void addBarel(Barrel_C_I bar) throws RemoteException {
        if (num_of_barrels == 0) {
            barrels.add(bar);
            active_barrel_idx.add(0);
            barrels.get(0).setName("barrel_0");
            num_of_barrels++;
            return;
        }
        for (int i = 0; i < num_of_barrels; i++) {
            if (!active_barrel_idx.contains(i)) {
                System.out.println("Barrel " + i + " no longer exists");
                barrels.set(i, bar);
                barrels.get(i).setName("barrel_" + i);
                active_barrel_idx.add(i);
                return;
            }
        }
        // if they are all alive
        barrels.add(bar);
        barrels.get(num_of_barrels).setName("barrel_" + num_of_barrels);
        active_barrel_idx.add(num_of_barrels);
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
            list_mutex.acquire();
            for (int i = 1; i < term.length; i++) {
                boolean hasElement = false;
                for (Object[] tuple : threadSafeList) {
                    if (term[i].toLowerCase().equals((String) tuple[0])) {
                        tuple[1] = (int) tuple[1] + 1;
                        hasElement = true;
                        break;
                    }
                }
                if (!hasElement) {
                    threadSafeList.add(new Object[]{term[i].toLowerCase(), 1});
                }
            }
            list_mutex.release();
        } catch (InterruptedException e) {
            System.out.println("SearchTop10: list mutex interrupted");
        }
        try {
            return barrels.get(0).searchTop10(term);
        } catch (RemoteException e) {
            System.out.println("Hahaha remote exception " + e);
            return null;
        }
    }

    // Get alive barrels name
    public String getAliveBarrelName() {
        StringBuilder info = new StringBuilder();

        for (int index : active_barrel_idx) {
            try {
                info.append(barrels.get(index).getName()).append(" - ");
                info.append(barrels.get(index).getAvgExeTime()).append("\n");
            } catch (RemoteException e) {
                continue;
            }
        }
        return info.toString();
    }
    
    public String getAdminInfo() throws RemoteException {
        StringBuilder info = new StringBuilder();
        
        info.append("--- Most searched words ---\n");
        try {
            list_mutex.acquire();
            threadSafeList.sort(new Comparator<Object[]>() {
                @Override
                public int compare(Object[] o1, Object[] o2) {
                    Integer int1 = (Integer) o1[1];
                    Integer int2 = (Integer) o2[1];
                    return int2.compareTo(int1);
                }
            });
            for (int i = 0; i < Math.min(10, threadSafeList.size()); i++) {
                // System.out.println(threadSafeList.get(i)[1]);
                info.append(threadSafeList.get(i)[0].toString()).append("\n");
            }
            list_mutex.release();
        } catch (InterruptedException e) {
            System.out.println("getAdminInfo: list mutex interrupted");
        }
        info.append("--- List of active barrels + Avg Exe time ---\n");
        info.append(getAliveBarrelName());

        return info.toString();
    }
}
