package project.resources;

import java.io.Serializable;

public class UrlInfo implements Serializable
{
    public String url;
    public String title;
    public String citation;
    public Integer termFrequency;

    public UrlInfo(String url, String title, String citation, Integer tFrequency) {
        this.url = url;
        this.title = title;
        this.citation = citation;
        this.termFrequency = tFrequency;
    }

    @Override
    public String toString()
    {
        return String.format(
            "Page with url: %s, title: %s, citation: %s, frequency: %d",
            url, title, citation, termFrequency
        );
    }
}
