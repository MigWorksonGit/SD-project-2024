import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.*;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class OLDGatewayServer extends UnicastRemoteObject implements OLDGateway_I
{
    //private DownloaderThread downloaders;
    private static final long serialVersionUID = 1L;
    static HashMap<String, OLDClient_I> clients = new HashMap<String, OLDClient_I>();

    // new QUEUE
    // Comunicar com Downloaders através de RMI
    public static LinkedBlockingQueue<String> Url_Queue = new LinkedBlockingQueue<>();

    public OLDGatewayServer() throws RemoteException {
		super();
	}

    //  Client RMI
    public void subscribe(String name, OLDClient_I client) throws RemoteException {
        System.out.println("> Subscribing " + name);
		System.out.print("> ");
		clients.put(name, client);
    }

    public void unsubscribe(String name, OLDClient_I client) throws RemoteException {
        System.out.println("> Unsubscribing " + name);
		System.out.print("> ");
        clients.remove(name);
    }

    // Downloaders RMI
    public void join_downloader(OLDDownloaderRMI_I downloader, String id) throws RemoteException {
        System.out.println("Joining Downloader " + id);
        downloader.get_URL_QUEUE_acess(Url_Queue, id);
    }

    public void receive_info(OLDMessage msg, OLDClient_I client) throws RemoteException
    {
		switch (msg.getAction()) {
            case "link":
                client.print_on_client(msg.getAction() + " " + msg.getMessage());
                break;
            case "search":
                Url_Queue.add(msg.getMessage());
                // send to queue queue.push(....)
                break;
            case "admin":
                // client.print_on_client(msg.getAction() + " " + msg.getMessage());
                break;
            default:
                break;
        }
	}

    public void debug_print(String debug) throws RemoteException {
        System.out.println("> Debug: " + debug);
        System.out.print("> ");
    }

    public static void main(String[] args)
    {
        try {
            OLDGatewayServer server = new OLDGatewayServer();
            LocateRegistry.createRegistry(1099).rebind("hello", server);
            System.out.println("Server is ready");
        }
        catch (RemoteException re) {
			System.out.println("Exception in GatewayServer.main: " + re);
		}
    }
}