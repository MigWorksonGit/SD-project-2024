import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

public class GatewayServer extends UnicastRemoteObject implements Gateway_I
{
    static Map<String, String> registeredUsers;

    public GatewayServer() throws RemoteException {
        super();
    }

    public static void main(String[] args) {
        try {
            GatewayServer server = new GatewayServer();
            LocateRegistry.createRegistry(1099).rebind("hello", server);
            System.out.println("Server is ready");
        }
        catch (RemoteException re) {
			System.out.println("Exception in GatewayServer.main: " + re);
		}
    }

    public void print_on_server(String msg) throws RemoteException {
        System.out.println(msg);
    }

}
