package project.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import project.resources.UrlInfo;

public interface Barrel_C_I extends Remote
{
    public List<String> getUrlsConnected2this(String url) throws RemoteException;
    public List<UrlInfo> searchTop10(String[] term) throws RemoteException;
    public boolean isAlive() throws RemoteException;
    public void setName(String newName) throws RemoteException;
    public String getName() throws RemoteException;
    public long getAvgExeTime() throws RemoteException;
}
