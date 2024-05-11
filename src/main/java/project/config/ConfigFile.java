package project.config;

import java.io.BufferedReader;
import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ConfigFile {
    // Dont forget to check if stuff is valid
    private String filepath = "src/main/java/project/config/config.json";
    private String IP;
    private String PORT;
    private String MulticastAddress;
    private String MulticastPort;

    public boolean getJsonInfo() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filepath));
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(bufferedReader, JsonObject.class);
            IP = json.get("IpAddress").getAsString();
            PORT = json.get("Port").getAsString();
            MulticastAddress = json.get("MulticastAddress").getAsString();
            MulticastPort = json.get("MulticastPort").getAsString();
        } catch (Exception e) {
            System.out.println("Json file does not exist");
            return false;
        }
        return true;
    }

    public String getIp() { return IP; }
    public String getPort() { return PORT; }
    public String getMulticastAddress() { return MulticastAddress; }
    public String getMulticastPort() { return MulticastPort; }
}
