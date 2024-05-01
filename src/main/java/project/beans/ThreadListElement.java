package project.beans;

import java.io.Serializable;

public class ThreadListElement implements Serializable {
    private String word;
    private int num_times_word_search;

    public ThreadListElement(String word, int num_times_word_search) {
        this.word = word;
        this.num_times_word_search = num_times_word_search;
    }

    // Setters
    public void SetWord(String word) {
        this.word = word;
    }
    public void SetNumTimesWordSearch(int num_times_word_search) {
        this.num_times_word_search = num_times_word_search;
    }

    // Getters
    public String GetWord() {
        return word;
    }
    public int GetNumTimesWordSearch() {
        return num_times_word_search;
    }
}
