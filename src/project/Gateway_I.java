package project;

import java.rmi.*;

public interface Gateway_I extends Remote
{
    public void print_on_server(String message) throws RemoteException;
}
