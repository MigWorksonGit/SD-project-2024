package project;

import java.io.Serializable;

import static project.DownloaderMain.*;

class DownloaderThread extends Thread implements Serializable
{
    // multicast stuff here

    String name;
    Thread thread;
    DownloaderThread(String name_t) {
        name = name_t;
        thread = new Thread(this, name);
        System.out.println("New downaloder Initialized! " + thread);
        thread.start();
    }

    public void run()
    {
        String url;
        try {
            while (true)
            {
                semaphore.acquire();
                url = URL_QUEUE.remove();
                // release semaphore here?
                // Porbably
                System.out.println("Downloader " + thread + " obtained URL: " + url);
            }
        }
        catch (Exception e) {
            System.out.println("Not poggers");
        }
    }
}
