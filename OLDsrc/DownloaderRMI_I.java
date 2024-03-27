import java.rmi.*;
import java.util.concurrent.LinkedBlockingQueue;

public interface DownloaderRMI_I extends Remote
{
    public void print_on_downloader(String msg, String id) throws RemoteException;
    public void get_URL_QUEUE_acess(LinkedBlockingQueue<String> Url_Queue, String id) throws RemoteException;
}