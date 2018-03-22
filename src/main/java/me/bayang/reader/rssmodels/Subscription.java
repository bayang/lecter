package me.bayang.reader.rssmodels;

import java.util.ArrayList;

public class Subscription implements Feed {
    
    private String id;
    private String title;
    private ArrayList<Categories> categories;
    private String sortid;
    private long firstitemmsec;
    private String url;
    private String htmlUrl;
    private String iconUrl;

    public Subscription() {
    }

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

    @Override
    public String getLabel() {
        return getTitle();
    }
    
    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCategories(ArrayList<Categories> categories) {
        this.categories = categories;
    }

    public void setSortid(String sortid) {
        this.sortid = sortid;
    }

    public void setFirstitemmsec(long firstitemmsec) {
        this.firstitemmsec = firstitemmsec;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Subscription [id=").append(id).append(", title=")
                .append(title).append(", categories=").append(categories)
                .append(", sortid=").append(sortid).append(", firstitemmsec=")
                .append(firstitemmsec).append(", url=").append(url)
                .append(", htmlUrl=").append(htmlUrl).append(", iconUrl=")
                .append(iconUrl).append("]");
        return builder.toString();
    }
    

}