package project.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import project.resources.UrlQueueElement;

public interface Downloader_I extends Remote
{
    public void print_on_server(String msg) throws RemoteException;
    public UrlQueueElement removeUrl2() throws RemoteException;
    public void indexUrl2(UrlQueueElement element) throws RemoteException;
    public String getMulticastAddress() throws RemoteException;
    public int getMulticastPort() throws RemoteException;
}
