package project.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Barrel_I extends Remote
{
    public void subscribeBarrel(Barrel_C_I barrel) throws RemoteException;
    public void removeBarrel(int index) throws RemoteException;
    public String getMulticastAddress() throws RemoteException;
    public int getMulticastPort() throws RemoteException;
}
