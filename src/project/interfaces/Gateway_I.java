package project.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Gateway_I extends Remote
{
    public void print_on_server(String msg) throws RemoteException;
    public String searchWord(String msg) throws RemoteException;
    public void addBarel(Barrel_C_I bar) throws RemoteException;
}
