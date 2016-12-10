package com.stefanie20.ReadDay.RSSModels;

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

