package project.Meta2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.HtmlUtils;

import project.Meta2.beans.Message;
import project.Meta2.beans.RMIbean;

@Controller
public class MessageController
{
    private final RMIbean server;

    @Autowired
    public MessageController(RMIbean server) {
        this.server = server;
    }

	@MessageMapping("/message")
	@SendTo("/topic/messages")
	public Message onMessage(Message message) throws InterruptedException {
		System.out.println("Message received " + message);
		Thread.sleep(1000); // simulated delay
		return new Message(HtmlUtils.htmlEscape(message.content()));
	}

    @GetMapping("/admin-info")
    public String adminInfo() {
        return "admin-info";
    }

    @GetMapping("/indexUrl")
    public String index(
        @RequestParam(name="url", required=true, defaultValue="-1") String url
    ) {
        server.indexUrl(url, "-1");
        return "indexUrl";
    }

    @GetMapping("/search")
    public String search() {
        return "search";
    }

    @GetMapping("/consult")
    public String consult() {
        return "consult";
    }

    @GetMapping("/greeting")
    public String greeting(
        @RequestParam(name="name", required=false, defaultValue="World") String name, Model model
    ) {
        model.addAttribute("name", name);
        server.printHelloWorld();
        return "greeting";
    }
}