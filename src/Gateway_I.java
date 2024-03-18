import java.rmi.*;

public interface Gateway_I extends Remote
{
    public void subscribe(String name, Client_I client) throws RemoteException;
    public void unsubscribe(String name, Client_I client) throws RemoteException;
    public void receive_info(Message m, Client_I client) throws RemoteException;
    public void debug_print(String debug) throws RemoteException;
}