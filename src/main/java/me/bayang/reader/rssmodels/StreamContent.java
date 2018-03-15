package me.bayang.reader.rssmodels;

import java.util.ArrayList;

/**
 * A StreamContent represents a object get from Inoreader's <a href="https://www.inoreader.com/developers/stream-contents">Stream Contents</a>
 */
public class StreamContent {
    private String direction;
    private String id;
    private String title;
    private String description;
    private Self self;
    private long updated;
    private String updatedUsec;
    private ArrayList<Item> items;
    private String continuation;
    
    public StreamContent() {
        
    }

    public StreamContent(String direction, String id, String title, String description, Self self, long updated, String updatedUsec, ArrayList<Item> items, String continuation) {
        this.direction = direction;
        this.id = id;
        this.title = title;
        this.description = description;
        this.self = self;
        this.updated = updated;
        this.updatedUsec = updatedUsec;
        this.items = items;
        this.continuation = continuation;
    }

    public String getDirection() {
        return direction;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Self getSelf() {
        return self;
    }

    public long getUpdated() {
        return updated;
    }

    public String getUpdatedUsec() {
        return updatedUsec;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public String getContinuation() {
        return continuation;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSelf(Self self) {
        this.self = self;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public void setUpdatedUsec(String updatedUsec) {
        this.updatedUsec = updatedUsec;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public void setContinuation(String continuation) {
        this.continuation = continuation;
    }
    
    

}



