package com.stefanie20.ReadDay;

import java.util.ArrayList;

/**
 * Created by F317 on 16/2/21.
 */
public class SubscriptionsList {
    private ArrayList<Subscription> subscriptions;

    public SubscriptionsList(ArrayList<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public ArrayList<Subscription> getSubscriptions() {
        return subscriptions;
    }
}
class Subscription{
    private String id;
    private String title;
    private ArrayList<Categories> categories;
    private String sortid;
    private long firstitemmsec;
    private String url;
    private String htmlUrl;
    private String iconUrl;

    public Subscription(String id, String title, ArrayList<Categories> categories, String sortid, long firstitemmsec, String url, String htmlUrl, String iconUrl) {
        this.id = id;
        this.title = title;
        this.categories = categories;
        this.sortid = sortid;
        this.firstitemmsec = firstitemmsec;
        this.url = url;
        this.htmlUrl = htmlUrl;
        this.iconUrl = iconUrl;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<Categories> getCategories() {
        return categories;
    }

    public String getSortid() {
        return sortid;
    }

    public long getFirstitemmsec() {
        return firstitemmsec;
    }

    public String getUrl() {
        return url;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }
}
class Categories{
    private String id;
    private String label;

    public Categories(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }
}
