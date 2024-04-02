package project.resources;

import java.io.Serializable;

public class UrlQueueElement implements Serializable
{
    public String url;
    public int recursion_level;
    public WebPage fatherPage;

    public UrlQueueElement(String url, int recursion_level, WebPage fatherPage) {
        this.url = url;
        this.recursion_level = recursion_level;
        this.fatherPage = fatherPage;
    }

    @Override
    public String toString()
    {
        return String.format(
            "Element with URL: %s, recursion_level: %d, Father page with url: %s", 
            url, recursion_level, fatherPage.url
        );
    }
}
