import java.rmi.*;

public interface Client_I extends Remote{
	public void print_on_client(String s) throws java.rmi.RemoteException;
}