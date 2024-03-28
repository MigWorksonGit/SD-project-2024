package project;

import java.io.Serializable;

public class Url implements Serializable {
    //private int id;
    private String url;
    private String title;
    private String citation;
    
    public Url(String url, String title, String citation) {
        //this.id = id;
        this.url = url;
        this.title = title;
        this.citation = citation;
    }
    /* 
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    */
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCitation() {
        return citation;
    }

    public void setCitation(String citation) {
        this.citation = citation;
    }

    @Override
    public String toString() {
        return "Url{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", citation='" + citation + '\'' +
                '}';
    }
}
