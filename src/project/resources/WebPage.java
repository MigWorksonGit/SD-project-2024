package project.resources;

import java.io.Serializable;

public class WebPage implements Serializable
{
    public String url;
    public String title;
    public String citation;
    public String fatherUrl;
    
    public WebPage(String url, String title, String citation, String fatherUrl) {
        this.url = url;
        this.title = title;
        this.citation = citation;
        this.fatherUrl = fatherUrl;
    }

    @Override
    public String toString()
    {
        return String.format(
            "Url: %s, Page Title: %s",
            url, title
        );
    }
}
