package project.resources;

import java.io.Serializable;

public class Message implements Serializable
{
    public String word;
    public WebPage page;

    public Message(String word, WebPage page) {
        this.word = word;
        this.page = page;
    }

    @Override
    public String toString()
    {
        return String.format(
            "Message with word: %s and WebPage with url: %s and %d references",
            word, page.url, page.number_of_fathers
        );
    }
}
