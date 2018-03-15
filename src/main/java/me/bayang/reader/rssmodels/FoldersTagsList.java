package me.bayang.reader.rssmodels;

import java.util.ArrayList;

/**
 * A FolderTagsList represents a object get from Inoreader's <a href="https://www.inoreader.com/developers/tag-list">Folders and tags list</a>
 */
public class FoldersTagsList {
    
    private ArrayList<Tag> tags;
    
    public FoldersTagsList() {
    }

    public FoldersTagsList(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("FoldersTagsList [tags=").append(tags).append("]");
        return builder.toString();
    }
    
}

