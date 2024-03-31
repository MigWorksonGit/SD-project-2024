package project;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import project.interfaces.Downloader_I;

public class Downloader
{
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

            // Do your stuff
            while (true)
            {
                String msg = server.removeUrl2();
                System.out.println(msg);
            }

        }
        catch (Exception e) {
            System.out.println("Exception in main: " + e);
        }
    }
}
