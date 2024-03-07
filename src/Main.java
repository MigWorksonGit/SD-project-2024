/*******************************************************************************
                                PACKAGES & LIBS                                * 
********************************************************************************/

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/*******************************************************************************
                                MAIN CLASS                                     * 
********************************************************************************/

class Index
{
    // For each word, the pages containing it
    HashMap<String, HashSet<String>> index = new HashMap<>();

    public void addElement(String key, String value)
    {
        HashSet<String> values = index.getOrDefault(key, new HashSet<>());
        values.add(value);
        index.put(key, values);
    }

    public void printElements()
    {
        // Print all elements of the HashMap
        for (Map.Entry<String, HashSet<String>> entry : index.entrySet()) {
            String key = entry.getKey();
            HashSet<String> values = entry.getValue();
            System.out.print(key + ": ");
            for (String value : values) {
                System.out.print(value + " ");
            }
            System.out.println();
        }
    }
}

public class Main
{
    static Index index = new Index();
    // Sample downloader
    static Downloader downloader = new Downloader();
    
    public static void main(String[] args)
    {
        // Insert an url into the database(?)
        String url = args[0];
        downloader.searchUrl(url, index);

        index.printElements();
    }
}
