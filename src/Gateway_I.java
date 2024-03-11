import java.rmi.*;

public interface Gateway_I extends Remote
{
    public void remote_print(Message m) throws RemoteException;
    public void remote_response(Message m) throws RemoteException;
    public void subscribe(String name, Client_I client) throws RemoteException;
    public void unsubscribe(String name, Client_I client) throws RemoteException;
}