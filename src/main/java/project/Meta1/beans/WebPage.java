package project.Meta1.beans;

import java.io.Serializable;

public class WebPage implements Serializable
{
    private String url;
    private String title;
    private String citation;
    private String fatherUrl;
    
    public WebPage(String url, String title, String citation, String fatherUrl) {
        this.url = url;
        this.title = title;
        this.citation = citation;
        this.fatherUrl = fatherUrl;
    }

    // Setters
    public void setCitation(String citation) {
        this.citation = citation;
    }

    // Getters
    public String getUrl() { return url; }
    public String getTitle() { return title; }
    public String getCitation() { return citation; }
    public String getFatherUrl() { return fatherUrl; }

    @Override
    public String toString()
    {
        return String.format(
            "Url: %s, Page Title: %s",
            url, title
        );
    }
}
