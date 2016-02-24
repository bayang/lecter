package com.stefanie20.ReadDay;

import java.util.ArrayList;

/**
 * Created by F317 on 16/2/21.
 */
public class FoldersTagsList {
    private ArrayList<Tag> tags;

    public FoldersTagsList(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }
}

class Tag implements Feed{
    private String id;
    private String sortid;

    public Tag(String id, String sortid) {
        this.id = id;
        this.sortid = sortid;
    }

    public String getId() {
        return id;
    }

    public String getSortid() {
        return sortid;
    }

    @Override
    public String toString() {
        return id.substring(id.lastIndexOf("/") + 1);
    }
}