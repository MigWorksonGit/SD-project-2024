package project.resources;

import java.io.Serializable;

public class UrlQueueElement implements Serializable
{
    public String url;
    public int recursion_level;

    public UrlQueueElement(String url, int recursion_level) {
        this.url = url;
        this.recursion_level = recursion_level;
    }

    @Override
    public String toString()
    {
        return String.format(
            "Element with URL: %s, recursion_level: %d", 
            url, recursion_level
        );
    }
}
