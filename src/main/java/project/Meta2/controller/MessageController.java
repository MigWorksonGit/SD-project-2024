package project.Meta2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.util.HtmlUtils;

import project.Meta2.beans.Message;
import project.Meta2.beans.RMIbean;

@Controller
public class MessageController
{
    private final RMIbean myBean;

    @Autowired
    public MessageController(RMIbean myBean) {
        this.myBean = myBean;
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

    @GetMapping("/greeting")
    public String redirect() {
        myBean.printHelloWorld();
        return "greeting";
    }
}