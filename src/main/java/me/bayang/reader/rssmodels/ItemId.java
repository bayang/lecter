package me.bayang.reader.rssmodels;

import java.util.ArrayList;

/**
 *  A ItemId represents a object get from Inoreader's <a href="https://www.inoreader.com/developers/item-ids">Item IDs</a>
 */
public class ItemId {
    private ArrayList items;
    private ArrayList<ItemRef> itemRefs;
    private String continuation;

    public ItemId() {
    }

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

    public void setItems(ArrayList items) {
        this.items = items;
    }

    public void setItemRefs(ArrayList<ItemRef> itemRefs) {
        this.itemRefs = itemRefs;
    }

    public void setContinuation(String continuation) {
        this.continuation = continuation;
    }
    
    
}