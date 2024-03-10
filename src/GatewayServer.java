import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.*;
import java.util.HashMap;

public class GatewayServer extends UnicastRemoteObject implements Gateway_I
{
    private static final long serialVersionUID = 1L;
    static HashMap<String, Client_I> clients = new HashMap<String, Client_I>();

    public GatewayServer() throws RemoteException {
		super();
	}

    public void remote_print(Message m) throws RemoteException {
		System.out.println("Server:" + m);
	}

    public void remote_response(Message m) throws RemoteException {
        return;
    }

    public void subscribe(String name, Client_I client) throws RemoteException
    {
        System.out.println("Subscribing " + name);
		System.out.print("> ");
		clients.put(name, client);
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