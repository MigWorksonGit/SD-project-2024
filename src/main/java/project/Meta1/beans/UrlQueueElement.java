package project.Meta1.beans;

import java.io.Serializable;

public class UrlQueueElement implements Serializable
{
    private String url;
    private int recursion_level;
    private String father_url;

    public UrlQueueElement(String url, int recursion_level, String fatherUrl) {
        this.url = url;
        this.recursion_level = recursion_level;
        this.father_url = fatherUrl;
    }

    // Getters
    public String getUrl() { return url; }
    public int getRecursionLvel() { return recursion_level; }
    public String getFatherUrl() { return father_url; }

    @Override
    public String toString()
    {
        return String.format(
            "Element with URL: %s, recursion_level: %d", 
            url, recursion_level
        );
    }
}
