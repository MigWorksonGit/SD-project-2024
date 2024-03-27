import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.server.*;
import java.util.Scanner;

public class OLDClient extends UnicastRemoteObject implements OLDClient_I
{
    OLDClient() throws RemoteException {
        super();
    }

    public void print_on_client(String s) throws RemoteException {
        System.out.println("> " + s);
    }

    public static void main(String[] args)
    {
    try (Scanner sc = new Scanner(System.in))
    {
        OLDGateway_I server = null;
        OLDClient client = new OLDClient();
        try {
            try {
                server = (OLDGateway_I) Naming.lookup("rmi://localhost:1099/hello");
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
            server.subscribe(args[0], (OLDClient_I)client);
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
            OLDMessage msg = new OLDMessage(div[0], div[1]);
            server.receive_info(msg, (OLDClient_I)client);
        }
    }
    catch (Exception e) {
        System.out.println("Exception in main: " + e);
    }
    System.exit(0);
    }
}