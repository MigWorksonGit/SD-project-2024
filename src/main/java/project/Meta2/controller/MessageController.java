package project.Meta2.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.HtmlUtils;

import project.Meta1.beans.UrlInfo;
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

    // @GetMapping("/greeting")
    // public String greeting(
    //     @RequestParam(name="name", required=false, defaultValue="World") String name, Model model
    // ) {
    //     model.addAttribute("name", name);
    //     server.printHelloWorld();
    //     return "greeting";
    // }
}