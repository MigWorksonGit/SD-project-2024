package project.resources;

public class WebPage
{
    public String url;
    public String title;
    public int references;
    
    public WebPage(String url, String title, int references) {
        this.url = url;
        this.title = title;
        this.references = references;
    }

    @Override
    public String toString()
    {
        return String.format("Url: %s, Page Title: %s, Num of References: %d", url, title, references);
    }
}
