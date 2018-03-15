package me.bayang.reader.rssmodels;

public class UnreadCounts{
    
    private String id;
    private int count;
    private String newestItemTimestampUsec;

    public UnreadCounts() {
    }

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

    public void setId(String id) {
        this.id = id;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setNewestItemTimestampUsec(String newestItemTimestampUsec) {
        this.newestItemTimestampUsec = newestItemTimestampUsec;
    }
    
    
}