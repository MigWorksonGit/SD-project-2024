package project.servers;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;

import project.GatewayServer;
import project.interfaces.Downloader_I;
import project.resources.UrlQueueElement;
import project.resources.WebPage;

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

    public boolean containsUrl(String url) throws RemoteException {
        return server.containsUrl(url);
    }

    public void addPage2Url(String url, WebPage fatherPage) throws RemoteException {
        server.addPage2Url(url, fatherPage);
    }

    public void putUrl(String url, HashSet<WebPage> temp_hash) throws RemoteException {
        server.putUrl(url, temp_hash);
    }
}
