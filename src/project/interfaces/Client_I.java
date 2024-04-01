package project.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Client_I extends Remote
{
    public void print_on_server(String msg) throws RemoteException;
    public void indexUrl(String url) throws RemoteException;
    public String searchWord(String msg) throws RemoteException;
}
