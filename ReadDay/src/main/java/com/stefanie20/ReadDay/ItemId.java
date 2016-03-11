package com.stefanie20.ReadDay;

import java.util.ArrayList;

/**
 * Created by F317 on 16/2/21.
 */
public class ItemId {
    private ArrayList items;
    private ArrayList<ItemRef> itemRefs;
    private String continuation;

    public ItemId(ArrayList items, ArrayList<ItemRef> itemRefs, String continuation) {
        this.items = items;
        this.itemRefs = itemRefs;
        this.continuation = continuation;
    }

    public ArrayList getItems() {
        return items;
    }

    public ArrayList<ItemRef> getItemRefs() {
        return itemRefs;
    }

    public String getContinuation() {
        return continuation;
    }
}

class ItemRef{
    private String id;
    private ArrayList<String> directStreamIds;
    private String timestampUsec;

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
}