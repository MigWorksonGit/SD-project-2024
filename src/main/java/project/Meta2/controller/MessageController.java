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
import project.Meta2.beans.HackerNewsInfo;
import project.Meta2.beans.InfoMessage;
import project.Meta2.beans.RMIbean;

@Controller
public class MessageController
{
    private final RMIbean server;

    @Autowired
    public MessageController(RMIbean server) {
        this.server = server;
    }

    @Autowired
    private SimpMessagingTemplate template;

	@MessageMapping("/message")
	@SendTo("/topic/messages")
	public InfoMessage onMessage(InfoMessage message) throws InterruptedException {
		System.out.println("Message received " + server.getAdminInfo());
		Thread.sleep(1000); // simulated delay
		return new InfoMessage(HtmlUtils.htmlEscape(server.getAdminInfo()));
	}

    @GetMapping("/admin-info")
    public String adminInfo() {
        return "admin-info";
    }

    @PostMapping("/indexUrl")
    public String indexUrl(
        @RequestParam("url") String url, Model model
    ) {
        server.indexUrl(url, "-1");
        model.addAttribute("url", url);
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
        List<UrlInfo> top10 = server.searchTop10_barrelPartition(input, currentPageInt);
        // Update websocket info
        this.template.convertAndSend("/topic/messages", new InfoMessage(server.getAdminInfo()));
        model.addAttribute("words", words);
        model.addAttribute("items", top10);
        model.addAttribute("currentPage", currentPageInt);
        return "search";
    }

    @PostMapping("/consult")
    public String consult(
        @RequestParam("url") String url, Model model
    ) {
        List<String> list = server.getUrlsConnected2this(url);
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
                                if (word.equals(input[i])) {
                                    server.indexUrl(item.url(), "-1");
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
}