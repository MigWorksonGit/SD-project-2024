package project;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;

import project.interfaces.Barrels_I;

public class Barrels extends UnicastRemoteObject implements Barrels_I
{
    // Index here
    static HashMap<String, HashSet<String>> url_list = new HashMap<>();

    // Multicast
    static String MULTICAST_ADDRESS = "230.0.0.1";
    static int MULTICAST_PORT = 4446;

    // Barrel variables
    String name;
    int port;

    public Barrels(String name, int port) throws RemoteException {
        super();
        this.name = name;
        this.port = port;
    }

    @SuppressWarnings("deprecation")   // because joinGroup gives a warning
    public static void main(String[] args)
    {
        Barrels barrel;
        try {
            // Dont forget to check for errors here!
            barrel = new Barrels(args[0], Integer.parseInt(args[1]));
            LocateRegistry.createRegistry(barrel.port).rebind("Barril_" + barrel.name, barrel);
            System.out.println("Barrel " + barrel.name + " Initialized with port: " + barrel.port);
        }
        catch (RemoteException re) {
			System.out.println("Exception in Barrels.main: " + re);
            System.exit(0);
		}

        MulticastSocket multicastSocket = null;
        try {
            multicastSocket = new MulticastSocket(MULTICAST_PORT);
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            multicastSocket.joinGroup(group);

            while (true)
            {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                multicastSocket.receive(packet);

                System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message:");
                String message = new String(packet.getData(), 0, packet.getLength());

                System.out.println(message);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            multicastSocket.close();
        }
    }
}
