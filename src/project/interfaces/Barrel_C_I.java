package project.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Barrel_C_I extends Remote
{
    public String getUrl(String url) throws RemoteException;
}
