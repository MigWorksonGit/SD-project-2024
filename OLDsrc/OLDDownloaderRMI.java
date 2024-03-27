import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.LinkedBlockingQueue;

public class OLDDownloaderRMI extends UnicastRemoteObject implements OLDDownloaderRMI_I
{
    public static LinkedBlockingQueue<String> Url_Queue;

    public OLDDownloaderRMI() throws RemoteException {
        super();
    }

    public void print_on_downloader(String msg, String id) throws RemoteException {
        System.out.println("Downloader " + id + "> " + msg);
    }

    public void get_URL_QUEUE_acess(LinkedBlockingQueue<String> Url_Queue, String id) throws RemoteException
    {
        // Problem: Queue taken is not updated when inserting values
        while (true)
        {
            try {
                if (Url_Queue.size() != 0) System.out.println(Url_Queue.take());
                String url = Url_Queue.take();
                System.out.println("Downloader " + id + " obtained url: " + url);
            }
            catch (Exception e) {
                System.out.println("Not poggers on downloader " + id);
            }
        }
    }

    public static void main(String[] args)
    {
        try {
            OLDGateway_I server = null;
            OLDDownloaderRMI downloader = new OLDDownloaderRMI();
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
                server.join_downloader((OLDDownloaderRMI_I)downloader, args[0]);
                System.out.println("Communication with server sucesfull");
            } catch (RemoteException e) {
                System.out.println("Error comunicating to the server");
                System.exit(0);
            }
        } catch (Exception e) {
            System.out.println("Exception in main: " + e);
        }
    }
}
