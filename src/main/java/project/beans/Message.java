package project.beans;

import java.io.Serializable;

public class Message implements Serializable
{
    private String word;
    private WebPage page;

    public Message(String word, WebPage page) {
        this.word = word;
        this.page = page;
    }

    // Setters
    public void setWord(String word) {
        this.word = word;
    }
    public void setPage(WebPage page) {
        this.page = page;
    }

    // Getters
    public String getWord() { return word; }
    public WebPage getPage() { return page; }

    @Override
    public String toString()
    {
        return String.format(
            "Message with word: %s and WebPage with url: %s",
            word, page.getUrl()
        );
    }
}
