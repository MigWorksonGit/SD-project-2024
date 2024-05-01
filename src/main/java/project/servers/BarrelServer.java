package project.servers;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import project.GatewayServer;
import project.interfaces.Barrel_C_I;
import project.interfaces.Barrel_I;

public class BarrelServer extends UnicastRemoteObject implements Barrel_I
{
    GatewayServer server;

    public BarrelServer(GatewayServer server) throws RemoteException {
        super();
        this.server = server;
    }

    public void subscribeBarrel(Barrel_C_I barrel) throws RemoteException {
        server.addBarel(barrel);
    }

    public void removeBarrel(int index) throws RemoteException {
        server.removeBarrel(index);
    }

    public String getMulticastAddress() throws RemoteException {
        return server.getMulticastAddress();
    }

    public int getMulticastPort() throws RemoteException {
        return server.getMulticastPort();
    }
}
