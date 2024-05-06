package project.Meta1.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import project.Meta1.beans.UrlInfo;

public interface Barrel_C_I extends Remote
{
    public List<String> getUrlsConnected2this(String url) throws RemoteException;
    public void setName(String newName) throws RemoteException;
    public String getName() throws RemoteException;
    public double getAvgExeTime() throws RemoteException;
    public List<UrlInfo> searchTop10_BarrelPartition(String[] term, int page) throws RemoteException;
}
