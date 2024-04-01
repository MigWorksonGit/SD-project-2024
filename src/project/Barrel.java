package project;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.MulticastSocket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;

import project.interfaces.Barrel_C_I;
import project.interfaces.Barrel_I;

public class Barrel extends UnicastRemoteObject implements Barrel_C_I
{
    // Multicast
    static String MULTICAST_ADDRESS = "230.0.0.1";
    static int MULTICAST_PORT = 4446;

    // Barrel variables
    String name;
    int port;

    // Index
    public static HashSet<String> index = new HashSet<>();

    public Barrel() throws RemoteException {
        super();
    }

    public static void main(String[] args) {
        try {
            // create new file
            // index will save all current data taken from downloader
            // once indexing is done, we save to the file?

            Barrel_I server = null;
            // Must make this also a unicast stuff RMI
            // Try and give correct error messages and such
            try {
                try {
                    server = (Barrel_I) Naming.lookup("rmi://localhost:1097/barrel");
                    // SUBSCRIBE BARREL TO SERVER!!!
                    Barrel myself  = new Barrel();
                    server.subscribeBarrel((Barrel_C_I) myself);
                }
                catch(MalformedURLException e) {
                    System.out.println("Server Url is incorrectly formed");
                    System.out.println("Cant comunicate with server...");
                    System.out.println("Closing...");
                    System.exit(0);
                }
                catch (NotBoundException e) {
                    System.out.println("Cant comunicate with server...");
                    System.out.println("Closing...");
                    System.exit(0);
                }
                System.out.println("Barrel is ready");
            } catch (RemoteException e) {
                System.out.println("Error comunicating to the server");
                System.exit(0);
            }

            MulticastSocket multicastSocket = null;
            try {
                multicastSocket = new MulticastSocket(MULTICAST_PORT);
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                // joinGroup is depreceated btw
                multicastSocket.joinGroup(group);

                while (true)
                {
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    multicastSocket.receive(packet);

                    System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message:");
                    // Offset is 6 because there is some random bytes before the start of the print message?
                    String message = new String(packet.getData(), 0, packet.getLength());
                    // if (!index.containsKey(message)) {
                    //     HashSet<String> temp = new HashSet<>();
                    //     temp.add(message);
                    //     index.put(message, temp);
                    // } else {
                    //     index.get(message).add(message);
                    // }
                    // THIS DOES NOT WORK
                    // if (!index.contains(message)) {
                    //     index.add(message);
                    // }

                    System.out.println(message);
                }
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                multicastSocket.close();
            }
        }
        catch (Exception e) {
            System.out.println("Exception in main: " + e);
        }
    }

    public String getUrl(String url) throws RemoteException {
        // for (String msg : index) {
        //     System.out.println(msg);
        // }
        if (index.contains(url)) {
            return url;
        }
        return "";
    }
}
