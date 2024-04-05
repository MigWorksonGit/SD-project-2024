package project.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import project.resources.UrlInfo;
import project.resources.UrlQueueElement;

public interface Client_I extends Remote
{
    public void print_on_server(String msg) throws RemoteException;
    public void indexUrl(UrlQueueElement element) throws RemoteException;
    public List<String> getUrlsConnected2this(String msg) throws RemoteException;
    public List<UrlInfo> searchTop10(String[] term) throws RemoteException;
}
