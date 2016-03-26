package com.stefanie20.ReadDay;

import java.util.ArrayList;

/**
 * A FolderTagsList represents a object get from Inoreader's <a href="https://www.inoreader.com/developers/tag-list">Folders and tags list</a>
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

}