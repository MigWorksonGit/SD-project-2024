package project.resources;

import java.io.Serializable;

public class UrlQueueElement implements Serializable
{
    public String url;
    public int recursion_level;
    public String father_url;

    public UrlQueueElement(String url, int recursion_level, String fatherUrl) {
        this.url = url;
        this.recursion_level = recursion_level;
        this.father_url = fatherUrl;
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
