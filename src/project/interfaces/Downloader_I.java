package project.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Downloader_I extends Remote
{
    public void sendUrlToDownloaders(String url) throws RemoteException;
}
