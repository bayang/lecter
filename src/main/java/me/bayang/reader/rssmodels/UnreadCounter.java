package me.bayang.reader.rssmodels;
import java.util.ArrayList;

/**
 * This class is used to get the counts of the unread items.
 */
public class UnreadCounter {
    
    private String max;
    private ArrayList<UnreadCounts> unreadcounts;
    
    public UnreadCounter() {
    }

    public UnreadCounter(String max, ArrayList<UnreadCounts> unreadcounts) {
        this.max = max;
        this.unreadcounts = unreadcounts;
    }

    public String getMax() {
        return max;
    }

    public ArrayList<UnreadCounts> getUnreadcounts() {
        return unreadcounts;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public void setUnreadcounts(ArrayList<UnreadCounts> unreadcounts) {
        this.unreadcounts = unreadcounts;
    }
    
    

}