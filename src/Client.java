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
        // TODO: put this on the server
        System.out.println("Welcome Aristocat");
        System.out.println("What would you like to do today?");
        System.out.println("1 -> Send Link");
        System.out.println("2 -> Search word");
        System.out.println("3 -> Admin Rights");
        System.out.println("4 -> Exit");
        int shouldExit = 0;
        do
        {
            System.out.print("> ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    String msg = sc.nextLine();
                    Message m = new Message(msg);
                    server.remote_print(m);
                    System.out.println("> Sending link");
                    break;
                case "2":
                    System.out.println("Searching name");
                    break;
                case "3":
                    System.out.println("Admining");
                    break;
                case "4":
                    shouldExit = 1;
                    break;
                default: break;
            }

            if (shouldExit == 1) {
                server.unsubscribe(args[0], (Client_I)client);
                break;
            }
        }
        while (true);
    }
    catch (Exception e) {
        System.out.println("Exception in main: " + e);
    }
    System.exit(0);
    }
}