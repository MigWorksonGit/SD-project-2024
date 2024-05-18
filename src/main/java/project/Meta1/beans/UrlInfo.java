package project.Meta1.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UrlInfo implements Serializable
{
    public String url;
    public String title;
    public String citation;
    public Integer termFrequency;
    public List<String> urlsPointing2this;

    public UrlInfo(String url, String title, String citation, Integer tFrequency) {
        this.url = url;
        this.title = title;
        this.citation = citation;
        this.termFrequency = tFrequency;
        this.urlsPointing2this = new ArrayList<>();
    }

    @Override
    public String toString()
    {
        return String.format(
            "%s\n%s\n%s",
            title, url, citation
        );
    }

    // Debug print
    // @Override
    // public String toString()
    // {
    //     return String.format(
    //         "%s\n%s\n%s\n%d",
    //         title, url, citation, termFrequency
    //     );
    // }
}
