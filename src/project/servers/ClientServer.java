package project.servers;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import project.GatewayServer;
import project.interfaces.Client_I;
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

    public String searchWord(String msg) throws RemoteException {
        return server.searchWord(msg);
    }
}
