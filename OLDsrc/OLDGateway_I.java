import java.rmi.*;

public interface OLDGateway_I extends Remote
{
    public void subscribe(String name, OLDClient_I client) throws RemoteException;
    public void unsubscribe(String name, OLDClient_I client) throws RemoteException;
    public void join_downloader(OLDDownloaderRMI_I downloader, String id) throws RemoteException;
    public void receive_info(OLDMessage m, OLDClient_I client) throws RemoteException;
    public void debug_print(String debug) throws RemoteException;
}