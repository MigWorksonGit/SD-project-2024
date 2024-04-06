package project.servers;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import project.GatewayServer;
import project.interfaces.Client_I;
import project.resources.UrlInfo;
import project.resources.UrlQueueElement;

public class ClientServer extends UnicastRemoteObject implements Client_I
{
    GatewayServer server;

    public ClientServer(GatewayServer server) throws RemoteException {
        super();
        this.server = server;
    }

    public void print_on_server(String msg) throws RemoteException {
        System.out.println(msg);
    }

    public void indexUrl(UrlQueueElement element) throws RemoteException {
        server.indexUrl(element);
    }

    public List<String> getUrlsConnected2this(String msg) throws RemoteException {
        return server.getUrlsConnected2this(msg);
    }

    public List<UrlInfo> searchTop10(String[] term) throws RemoteException {
        return server.searchTop10(term);
    }

    public String getAdminInfo() throws RemoteException {
        return server.getAdminInfo();
    }
}
