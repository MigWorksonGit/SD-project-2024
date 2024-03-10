import java.rmi.*;
import java.rmi.server.*;
import java.util.Scanner;

public class Client extends UnicastRemoteObject implements Client_I
{
    Client() throws RemoteException {
        super();
    }

    public void print_on_client(String s) throws RemoteException {
        System.out.println("> " + s);
    }

    public static void main(String[] args)
    {
        String msg;
        try (Scanner sc = new Scanner(System.in)) {
            Gateway_I server = (Gateway_I) Naming.lookup("rmi://localhost:1099/hello");
            Client client = new Client();
            server.subscribe("Miguel", (Client_I)client);
            System.out.println("Client sent subscription to server");
            while (true) {
				System.out.print("> ");
				msg = sc.nextLine();
                Message m = new Message(msg);
				server.remote_print(m);
			}
        }
        catch (Exception e) {
            System.out.println("Exception in main: " + e);
        }
    }
}