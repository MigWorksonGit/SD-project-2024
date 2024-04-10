package project.servers;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import project.GatewayServer;
import project.interfaces.Downloader_I;
import project.resources.UrlQueueElement;

public class DownloaderServer extends UnicastRemoteObject implements Downloader_I
{
    GatewayServer server;

    public DownloaderServer(GatewayServer server) throws RemoteException {
        super();
        this.server = server;
    }

    public void print_on_server(String msg) throws RemoteException {
        System.out.println(msg);
    }

    public UrlQueueElement removeUrl2() throws RemoteException {
        return server.removeUrl();
    }

    public void indexUrl2(UrlQueueElement element) throws RemoteException {
        server.indexUrl(element);
    }

    public String getMulticastAddress() throws RemoteException {
        return server.getMulticastAddress();
    }

    public int getMulticastPort() throws RemoteException {
        return server.getMulticastPort();
    }
}
