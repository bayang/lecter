package com.stefanie20.ReadDay;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used to get the counts of the unread items.
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

    public static Map<String,Integer> getUnreadCountsMap() {
        HashMap<String, Integer> map = new HashMap<>();
        Gson gson = new Gson();
        UnreadCounter counter = gson.fromJson(ConnectServer.connectServer(ConnectServer.unreadCountURL), UnreadCounter.class);
        List<UnreadCounts> counts = counter.getUnreadcounts();
        for (UnreadCounts count : counts) {
            map.put(count.getId(), count.getCount());
        }
        map.put("All Items", counts.get(0).getCount());
        return map;
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