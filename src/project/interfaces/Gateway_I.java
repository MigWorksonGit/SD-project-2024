package project.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Gateway_I extends Remote
{
    public void print_on_server(String msg) throws RemoteException;
    public List<String> getUrlsConnected2this(String msg) throws RemoteException;
    public void addBarel(Barrel_C_I bar) throws RemoteException;
}
