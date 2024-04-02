package project.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashSet;

import project.resources.UrlQueueElement;
import project.resources.WebPage;

public interface Downloader_I extends Remote
{
    public void print_on_server(String msg) throws RemoteException;
    public UrlQueueElement removeUrl2() throws RemoteException;
    public void indexUrl2(UrlQueueElement element) throws RemoteException;

    public boolean containsUrl(String url) throws RemoteException;
    public void addPage2Url(String url, WebPage fatherPage) throws RemoteException;
    public void putUrl(String url, HashSet<WebPage> temp_hash) throws RemoteException;
}
