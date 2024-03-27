import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.net.DatagramPacket;
import java.net.InetAddress;

import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;

public class OLDBarrel {
    
    private static String MULTICAST_ADDRESS = "230.0.0.1";
    private static int PORT = 4446;

    public OLDBarrel() throws RemoteException {
        super();
    }

    public static void main(String[] args) throws ClassNotFoundException {
        
        

       
        try {
            MulticastSocket socket = new MulticastSocket(PORT);
            InetAddress multicastGroup = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(multicastGroup);
            while (true) {
                byte[] buffer = new byte[5000];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
                ObjectInputStream ois = new ObjectInputStream(bais);
                OLDUrl url = (OLDUrl) ois.readObject();
                url.toString();
            }
        } catch (Exception e) { 
            e.printStackTrace();
        }
    }
}
