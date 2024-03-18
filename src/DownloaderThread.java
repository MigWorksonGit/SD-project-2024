import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.LinkedBlockingQueue;

public class DownloaderThread extends Thread
{
    public LinkedBlockingQueue<String> Url_Queue = new LinkedBlockingQueue<>();
    static HashMap<String, HashSet<String>> index = new HashMap<>();

    // DOwnloader = new Downloader(URL_QUEUE)
    // DOwnloader = new Downloader(URL_QUEUE)
    // DOwnloader = new Downloader(URL_QUEUE)
    // DOwnloader = new Downloader(URL_QUEUE)
    // DOwnloader = new Downloader(URL_QUEUE)

    // TERMINAINS DIFERENTES
    // fgh

    public DownloaderThread() {}

    @Override
    public void run() {
        try {
            while (true)
            {
                String item = Url_Queue.take();
                System.out.println(item);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void push(String msg) {
        Url_Queue.add(msg);
    }
    
    public static void main(String[] args)
    {
        DownloaderThread downloader = new DownloaderThread();
        downloader.start();
    }
}
