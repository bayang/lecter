package me.bayang.reader.rssmodels;

import java.util.ArrayList;

public class ItemRef{
    
    private String id;
    private ArrayList<String> directStreamIds;
    private String timestampUsec;

    public ItemRef() {
    }

    public ItemRef(String id, ArrayList<String> directStreamIds, String timestampUsec) {
        this.id = id;
        this.directStreamIds = directStreamIds;
        this.timestampUsec = timestampUsec;
    }

    public String getId() {
        return id;
    }

    public ArrayList<String> getDirectStreamIds() {
        return directStreamIds;
    }

    public String getTimestampUsec() {
        return timestampUsec;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDirectStreamIds(ArrayList<String> directStreamIds) {
        this.directStreamIds = directStreamIds;
    }

    public void setTimestampUsec(String timestampUsec) {
        this.timestampUsec = timestampUsec;
    }
    
    
}