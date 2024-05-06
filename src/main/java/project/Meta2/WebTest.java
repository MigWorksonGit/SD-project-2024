package project.Meta2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import project.Meta1.interfaces.Client_I;

@SpringBootApplication
public class WebTest implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(WebTest.class, args);
    }

    @Override 
    public void run(String... args) throws Exception {
        // Dont forget to check if stuff is valid
        String filepath = "config/config.json";
        String IP = null;
        String PORT = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filepath));
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(bufferedReader, JsonObject.class);
            IP = json.get("IpAddress").getAsString();
            PORT = json.get("Port").getAsString();
        } catch (Exception e) {
            System.out.println("Json file does not exist");
            System.exit(0);
        }

        // RMI connection
        String lookup = "rmi://" + IP + ":" + PORT + "/client";
        Client_I server = null;
        try {
            try {
                server = (Client_I) Naming.lookup(lookup);
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
            System.out.println("Client sent subscription to server");
        } catch (RemoteException e) {
            System.out.println("Error comunicating to the server");
            System.exit(0);
        }

        server.print_on_server("kek");
    }
}
