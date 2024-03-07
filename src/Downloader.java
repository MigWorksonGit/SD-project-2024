/*******************************************************************************
                                PACKAGES & LIBS                                * 
********************************************************************************/

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.StringTokenizer;

/*******************************************************************************
                                MAIN CLASS                                     * 
********************************************************************************/

public class Downloader
{
    public void searchUrl(String url, Index index)
    {
        try {
            Document doc = Jsoup.connect(url).get();
            StringTokenizer tokens = new StringTokenizer(doc.text());
            int countTokens = 0;
            // while (tokens.hasMoreElements() && countTokens++ < 100)
            //     System.out.println(tokens.nextToken().toLowerCase());
            Elements links = doc.select("a[href]");
            for (Element link : links) {
                // System.out.println(link.text() + "\n" + link.attr("abs:href") + "\n");
                index.addElement(link.text(), link.attr("abs:href"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}