package project.resources;

import java.io.Serializable;

public class WebPage implements Serializable
{
    public String url;
    public String title;
    public String citation;
    public int number_of_fathers;
    
    public WebPage(String url, String title, String citation, int number_of_fathers) {
        this.url = url;
        this.title = title;
        this.citation = citation;
        this.number_of_fathers = number_of_fathers;
    }

    @Override
    public String toString()
    {
        return String.format(
            "Url: %s, Page Title: %s with %d references",
            url, title, number_of_fathers
        );
    }
}
