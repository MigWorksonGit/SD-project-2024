package project.resources;

import java.io.Serializable;

public class WebPage implements Serializable
{
    public String word;
    public String url;
    public String title;
    public String citation;
    
    public WebPage(String word, String url, String title, String citation) {
        this.word = word;
        this.url = url;
        this.title = title;
        this.citation = citation;
    }

    @Override
    public String toString()
    {
        return String.format("Url: %s, Page Title: %s", url, title);
    }
}
