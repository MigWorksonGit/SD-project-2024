package project.Meta2.controller;

import java.util.List;
import java.util.StringTokenizer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.HtmlUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import project.Meta1.beans.UrlInfo;
import project.Meta1.beans.UrlQueueElement;
import project.Meta1.interfaces.Client_I;
import project.Meta2.beans.HackerNewsInfo;
import project.Meta2.beans.InfoMessage;
import project.Meta2.interfaces.WebClient_I;
import project.config.ConfigFile;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.generativeai.GenerativeModel;

import jakarta.annotation.PostConstruct;

import com.google.cloud.vertexai.api.GenerateContentResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

@Controller
public class MessageController extends UnicastRemoteObject implements WebClient_I
{
    private Client_I server;
    int DEBUG_recursion_level = 10;

    public MessageController() throws RemoteException {
        super();
        ConfigFile data = new ConfigFile();
        data.getJsonInfo();
        String lookup = "rmi://" + data.getIp() + ":" + data.getPort() + "/client";
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
    }
    
    @PostConstruct
    public void init() {
        System.out.println("Construting Postttt");
        try {
            server.subscribeWebClient(this);
        } catch (RemoteException e) {
            System.out.println("WHy god why");
        }
    }

    @Autowired
    private SimpMessagingTemplate template;

	@MessageMapping("/message")
	@SendTo("/topic/messages")
	public InfoMessage onMessage(InfoMessage message) throws InterruptedException {
        try {
            System.out.println("Message received " + server.getAdminInfo());
            Thread.sleep(1000); // simulated delay
            return new InfoMessage(HtmlUtils.htmlEscape(server.getAdminInfo()));
        } 
        catch (RemoteException e) {
            System.out.println("Faied to retrive information");
            return new InfoMessage(HtmlUtils.htmlEscape("Failed to retrive information from server"));
        }
	}

    @GetMapping("/admin-info")
    public String adminInfo() {
        return "admin-info";
    }

    @PostMapping("/indexUrl")
    public String indexUrl(
        @RequestParam("url") String url, Model model
    ) {
        try {
            server.indexUrl(new UrlQueueElement(url, DEBUG_recursion_level, "-1"));
            model.addAttribute("url", url);
        } catch (RemoteException e) {
            System.out.println("Failed to connect to server");
        }
        return "indexUrl";
    }

    @PostMapping("/search")
    public String search(
        @RequestParam("words") String words,
        @RequestParam(name="currentPage", defaultValue = "0") String currentPage,
        Model model
    ) {
        int currentPageInt = Integer.parseInt(currentPage); // Convert currentPage to int
        String[] input = words.trim().split(" ");

        List<UrlInfo> top10 = null;
        try {
            String temp = "temp ";
            for (int i = 0; i < input.length; i++) {
                temp += input[i];
            }
            String[] term = temp.trim().split(" ");
            top10 = server.searchTop10_BarrelPartition(term, currentPageInt);
        } catch (RemoteException e ) {
            System.out.println("Failed to connect to server/barrel");
        }
        // Update websocket info
        // this.template.convertAndSend("/topic/messages", new InfoMessage(server.getAdminInfo()));
        model.addAttribute("words", words);
        model.addAttribute("items", top10);
        model.addAttribute("currentPage", currentPageInt);
        return "search";
    }

    @PostMapping("/consult")
    public String consult(
        @RequestParam("url") String url, Model model
    ) {
        List<String> list = null;
        try {
            list = server.getUrlsConnected2this(url);
        } catch (RemoteException e) {
            System.out.println("Failed to connect to server");
        }
        model.addAttribute("url", url);
        model.addAttribute("items", list);
        return "consult";
    }

    @PostMapping("/hackernews")
    public String indexTopStories(
        @RequestParam("words") String words,
        Model model
    ) {
        String topStoriesEndpoint = "https://hacker-news.firebaseio.com/v0/topstories.json";
        RestTemplate restTemplate = new RestTemplate();
        String topStoriesIDs = restTemplate.getForObject(topStoriesEndpoint, String.class);
        
        StringTokenizer tokenizer = new StringTokenizer(topStoriesIDs,"[,]");

        String[] input = words.trim().split(" ");

        int maxTokens = 0;
        try {
            while (tokenizer.hasMoreTokens() && maxTokens++ < 10)
            {
                int storyID = Integer.parseInt(tokenizer.nextToken());
                String userStoryEndpoint = "https://hacker-news.firebaseio.com/v0/item/" + storyID +".json";
                String userStory = restTemplate.getForObject(userStoryEndpoint, String.class);

                ObjectMapper objectMapper = new ObjectMapper();
                HackerNewsInfo item = objectMapper.readValue(userStory, HackerNewsInfo.class);

                if (item.type().equals("story")) {
                    if (item.url() != null) {
                        // System.out.println(item.url());

                        Document doc = Jsoup.connect(item.url()).get();
                        StringTokenizer tokens = new StringTokenizer(doc.text());
                        loop:
                        while (tokens.hasMoreElements()) {
                            String word = tokens.nextToken().strip().toLowerCase().replaceAll("[.,;:\\\"'?!|«»()\\[\\]{}-]", "");
                            if (!word.matches("[a-zA-Z].*")) {
                                continue;
                            }
                            for (int i = 0; i < input.length; i++) {
                                if (word.equals(input[i]))
                                {
                                    try {
                                        server.indexUrl(new UrlQueueElement(item.url(), DEBUG_recursion_level, "-1"));
                                    } catch (RemoteException e) {
                                        System.out.println("Failed to connect to server");
                                    }
                                    System.out.println(item.url());
                                    break loop;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Failed hahaha");
        }
        return "hackernews";
    }

    @GetMapping("/test")
    public String test() {
        String PROJECT_ID = "19552739649";
        String LOCATION = "us-central1";

        try {
            VertexAI vertexAI = new VertexAI(PROJECT_ID, LOCATION);

            GenerativeModel model = new GenerativeModel("gemini-pro", vertexAI);
            GenerateContentResponse response = model.generateContent("How are you?");
            System.out.println(response.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "admin-info";
    }

    public void print_on_webserver() throws RemoteException {
        // System.out.println("Hello World!");
        this.template.convertAndSend("/topic/messages", new InfoMessage(server.getAdminInfo()));
    }
}