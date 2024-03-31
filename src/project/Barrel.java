package project;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.MulticastSocket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import project.interfaces.Barrel_I;

public class Barrel
{
    // Multicast
    static String MULTICAST_ADDRESS = "230.0.0.1";
    static int MULTICAST_PORT = 4446;

    // Barrel variables
    String name;
    int port;

    public static void main(String[] args) {
        try {
            Barrel_I server = null;
            // Try and give correct error messages and such
            try {
                try {
                    server = (Barrel_I) Naming.lookup("rmi://localhost:1097/barrel");
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
                multicastSocket.joinGroup(group);

                while (true)
                {
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    multicastSocket.receive(packet);

                    System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message:");
                    // Offset is 6 because there is some random bytes before the start of the print message?
                    String message = new String(packet.getData(), 6, packet.getLength());

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
}
