package project.Meta1.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import project.Meta1.beans.UrlInfo;
import project.Meta1.beans.UrlQueueElement;
import project.Meta2.interfaces.WebClient_I;

public interface Client_I extends Remote
{
    public void print_on_server(String msg) throws RemoteException;
    public void indexUrl(UrlQueueElement element) throws RemoteException;
    public List<String> getUrlsConnected2this(String msg) throws RemoteException;
    public String getAdminInfo() throws RemoteException;
    public List<UrlInfo> searchTop10_BarrelPartition(String[] term, int page) throws RemoteException;

    public void subscribeWebClient(WebClient_I webclient) throws RemoteException;
}
