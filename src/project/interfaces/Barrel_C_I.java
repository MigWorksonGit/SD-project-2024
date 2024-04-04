package project.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import project.resources.UrlInfo;

public interface Barrel_C_I extends Remote
{
    public String getUrl(String url) throws RemoteException;
    public List<UrlInfo> searchTop10(String[] term) throws RemoteException;
}
