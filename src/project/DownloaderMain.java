package project;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

public class DownloaderMain extends UnicastRemoteObject implements Downloader_I
{
    // Data structure with how many downloaders we want
    static DownloaderThread[] downloaders;
    // Queue
    static ConcurrentLinkedQueue<String> URL_QUEUE;
    static Semaphore semaphore;

    public DownloaderMain() throws RemoteException {
        super();
    }

    static boolean validArgument(String arg) {
        try {
            int num = Integer.parseInt(arg);
            if (num > 0) return true;
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // args[0] is how many downloaders we want
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Requires 1 argument (Number of downloaders)");
            System.exit(0);
        }
        if (!validArgument(args[0])) {
            System.out.println("Invalid input. Must be > 0 and an Integer");
            System.exit(0);
        }
        int num = Integer.parseInt(args[0]);
        downloaders = new DownloaderThread[num];
        semaphore = new Semaphore(0);
        URL_QUEUE = new ConcurrentLinkedQueue<String>();

        try {
            DownloaderMain downloaderMain = new DownloaderMain();
            LocateRegistry.createRegistry(1098).rebind("downloaders", downloaderMain);
            System.out.println("Downloaders initialization is ready");

            for (int i = 0; i < num; ++i) {
                downloaders[i] = new DownloaderThread(String.valueOf(i));
            }
        }
        catch (RemoteException re) {
			System.out.println("Exception in DownloaderMain.main: " + re);
		}
    }

    public void sendUrlToDownloaders(String url) throws RemoteException
    {
        URL_QUEUE.add(url);
        semaphore.release();
    }
}
