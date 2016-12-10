package com.stefanie20.ReadDay.RSSModels;

import com.google.gson.Gson;
import com.stefanie20.ReadDay.Controllers.ConnectServer;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

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

    public static List<Item> getStreamContent(String URLString) {
        BufferedReader reader = ConnectServer.connectServer(URLString);
        Gson gson = new Gson();
        StreamContent content = gson.fromJson(reader, StreamContent.class);
        List<Item> itemList = new ArrayList<>(content.getItems());

        while (content.getContinuation() != null) {
            reader = ConnectServer.connectServer(URLString + "&c=" + content.getContinuation());
            content = gson.fromJson(reader, StreamContent.class);
            itemList.addAll(content.getItems());
        }
        return itemList;
    }


    public static void main(String[] args) throws Exception{
        List<Item> list = getStreamContent(ConnectServer.starredContentURL);
        PrintWriter pw = new PrintWriter("output.txt");
        for (Item item : list) {
            pw.println(item.getTitle());
            pw.println(item.getCategories());
        }
        pw.close();
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

}

class Self {
    private String href;

    public Self(String href) {
        this.href = href;
    }

    public String getHref() {
        return href;
    }
}




class Alternate{
    private String href;
    private String type;

    public Alternate(String href, String type) {
        this.href = href;
        this.type = type;
    }

    public String getHref() {
        return href;
    }

    public String getType() {
        return type;
    }
}



