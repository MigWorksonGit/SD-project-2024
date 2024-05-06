package project.Meta1.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import project.Meta1.beans.UrlQueueElement;

public interface Downloader_I extends Remote
{
    public String getMulticastAddress() throws RemoteException;
    public int getMulticastPort() throws RemoteException;
    public UrlQueueElement removeUrl() throws RemoteException;
    public void indexUrl(UrlQueueElement element) throws RemoteException;
}
