package project.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Barrel_I extends Remote
{
    public void print_on_server(String msg) throws RemoteException;
    public void subscribeBarrel(Barrel_C_I barrel) throws RemoteException;
}
