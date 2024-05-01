package project.servers;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import project.GatewayServer;
import project.beans.UrlInfo;
import project.beans.UrlQueueElement;
import project.interfaces.Client_I;

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
}
