package project.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Gateway_I extends Remote
{
    public void print_on_server(String msg) throws RemoteException;
}
