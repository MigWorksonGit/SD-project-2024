package project.servers;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import project.GatewayServer;
import project.beans.UrlQueueElement;
import project.interfaces.Downloader_I;

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
