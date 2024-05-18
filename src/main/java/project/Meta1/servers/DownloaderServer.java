package project.Meta1.servers;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import project.Meta1.GatewayServer;
import project.Meta1.beans.UrlQueueElement;
import project.Meta1.interfaces.Downloader_I;

public class DownloaderServer extends UnicastRemoteObject implements Downloader_I
{
    GatewayServer server;

    public DownloaderServer(GatewayServer server) throws RemoteException {
        super();
        this.server = server;
    }

    public String getMulticastAddress() throws RemoteException {
        return server.getMulticastAddress();
    }

    public int getMulticastPort() throws RemoteException {
        return server.getMulticastPort();
    }

    public UrlQueueElement removeUrl() throws RemoteException {
        return server.removeUrl();
    }

    public void indexUrl(UrlQueueElement element) throws RemoteException {
        server.indexUrl(element);
    }
}
