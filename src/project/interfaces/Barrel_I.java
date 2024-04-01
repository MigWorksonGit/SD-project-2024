package project.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import project.Barrel;

public interface Barrel_I extends Remote
{
    public void print_on_server(String msg) throws RemoteException;
    public void subscribeBarrel(Barrel barrel) throws RemoteException;
}
