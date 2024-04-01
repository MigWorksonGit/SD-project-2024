package project;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.MulticastSocket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import project.interfaces.Downloader_I;

public class Downloader
{
    private static String MULTICAST_ADDRESS = "230.0.0.1";
    private static int MULTICAST_PORT = 4446;

    public static void main(String[] args) {
        try {
            Downloader_I server = null;
            // Try and give correct error messages and such
            try {
                try {
                    server = (Downloader_I) Naming.lookup("rmi://localhost:1098/downloader");
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
                System.out.println("Downloader is ready");
            } catch (RemoteException e) {
                System.out.println("Error comunicating to the server");
                System.exit(0);
            }

            MulticastSocket multicastSocket = null;
            String url;
            // Do your stuff
            try {
                multicastSocket = new MulticastSocket();
                while (true)
                {
                    url = server.removeUrl2();
                    DEBUG_testMulticast(url, multicastSocket);
                    System.out.println("Downloader obtained URL: " + url);
                }
            } catch (IOException e) {
                System.out.println("Error while creating multicast");
            } 
            finally {
                multicastSocket.close();
            }
        }
        catch (Exception e) {
            System.out.println("Exception in main: " + e);
        }
    }

    static void DEBUG_testMulticast(String url, MulticastSocket multicastSocket) {
        try
        {
            // ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // ObjectOutputStream oos = new ObjectOutputStream(baos);
            // oos.writeObject(url);
            // oos.flush();
            // byte[] data = baos.toByteArray();
            byte[] data = url.getBytes();

            InetAddress multicastAddress = InetAddress.getByName(MULTICAST_ADDRESS);

            // Send the byte array via multicast
            DatagramPacket packet = new DatagramPacket(data, data.length, multicastAddress, MULTICAST_PORT);
            multicastSocket.send(packet);
        }
        catch (IOException e) {
            System.out.println("Error while reading stream header");
        }
    }
}
