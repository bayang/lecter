package com.stefanie20.ReadDay.RSSModels;

import java.util.ArrayList;

/**
 * Created by zz on 2016/12/10.
 */
public class Subscription implements Feed {
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