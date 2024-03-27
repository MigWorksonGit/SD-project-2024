import java.net.MalformedURLException;
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
    try (Scanner sc = new Scanner(System.in))
    {
        Gateway_I server = null;
        Client client = new Client();
        try {
            try {
                server = (Gateway_I) Naming.lookup("rmi://localhost:1099/hello");
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
            server.subscribe(args[0], (Client_I)client);
            System.out.println("Client sent subscription to server");
        } 
        catch (RemoteException e) {
            System.out.println("Error comunicating to the server");
            System.exit(0);
        }

        while (true)
        {
            System.out.print("> ");
            String input = sc.nextLine();
            String[] div = input.split(" ");
            if (div.length != 2) {
                System.out.println("Bad command");
                continue;
            }
            Message msg = new Message(div[0], div[1]);
            server.receive_info(msg, (Client_I)client);
        }
    }
    catch (Exception e) {
        System.out.println("Exception in main: " + e);
    }
    System.exit(0);
    }
}