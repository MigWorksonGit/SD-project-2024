package project.Meta2.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface WebClient_I extends Remote
{
    public void print_on_webserver() throws RemoteException;
}
