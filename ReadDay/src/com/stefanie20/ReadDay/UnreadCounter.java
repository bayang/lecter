package com.stefanie20.ReadDay;
import java.util.ArrayList;

/**
 * Created by F317 on 16/2/21.
 */
public class UnreadCounter {
    private String max;
    private ArrayList<UnreadCounts> unreadcounts;

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
}

class UnreadCounts{
    private String id;
    private int count;
    private String newestItemTimestampUsec;

    public UnreadCounts(String id, int count, String newestItemTimestampUsec) {
        this.id = id;
        this.count = count;
        this.newestItemTimestampUsec = newestItemTimestampUsec;
    }

    public String getId() {
        return id;
    }

    public int getCount() {
        return count;
    }

    public String getNewestItemTimestampUsec() {
        return newestItemTimestampUsec;
    }
}