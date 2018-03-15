package me.bayang.reader.rssmodels;

/**
 * An interface created to combine Tag, Categories and Subscription, used in the TreeView.
 */
public interface Feed {
    String getId();
    String getSortid();
    String getLabel();
}