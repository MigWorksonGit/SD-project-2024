import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.*;
import java.util.HashMap;

public class GatewayServer extends UnicastRemoteObject implements Gateway_I
{
    //private DownloaderThread downloaders;
    private static final long serialVersionUID = 1L;
    static HashMap<String, Client_I> clients = new HashMap<String, Client_I>();

    // new QUEUE

    public GatewayServer() throws RemoteException {
		super();
	}

    public void subscribe(String name, Client_I client) throws RemoteException {
        System.out.println("> Subscribing " + name);
		System.out.print("> ");
		clients.put(name, client);
    }

    public void unsubscribe(String name, Client_I client) throws RemoteException {
        System.out.println("> Unsubscribing " + name);
		System.out.print("> ");
        clients.remove(name);
    }

    public void receive_info(Message msg, Client_I client) throws RemoteException
    {
		switch (msg.getAction()) {
            case "link":
                client.print_on_client(msg.getAction() + " " + msg.getMessage());
                break;
            case "search":
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
            GatewayServer server = new GatewayServer();
            LocateRegistry.createRegistry(1099).rebind("hello", server);
            System.out.println("Server is ready");
        }
        catch (RemoteException re) {
			System.out.println("Exception in GatewayServer.main: " + re);
		}


    }
}