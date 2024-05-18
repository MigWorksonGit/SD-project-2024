package project.Meta1.servers;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import project.Meta1.GatewayServer;
import project.Meta1.beans.UrlInfo;
import project.Meta1.beans.UrlQueueElement;
import project.Meta1.interfaces.Client_I;
import project.Meta2.interfaces.WebClient_I;

public class ClientServer extends UnicastRemoteObject implements Client_I
{
    GatewayServer server;

    public ClientServer(GatewayServer server) throws RemoteException {
        super();
        this.server = server;
    }

    public void print_on_server(String msg) throws RemoteException {
        server.print_on_server(msg);
    }

    public void indexUrl(UrlQueueElement element) throws RemoteException {
        server.indexUrl(element);
    }

    public List<String> getUrlsConnected2this(String msg) throws RemoteException {
        return server.getUrlsConnected2this(msg);
    }

    public String getAdminInfo() throws RemoteException {
        return server.getAdminInfo();
    }

    public List<UrlInfo> searchTop10_BarrelPartition(String[] term, int page) throws RemoteException {
        return server.searchTop10_barrelPartition(term, page);
    }

    public void subscribeWebClient(WebClient_I webclient) throws RemoteException {
        server.subscribeWebClient(webclient);
    }
}
