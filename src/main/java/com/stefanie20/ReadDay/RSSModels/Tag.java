package com.stefanie20.ReadDay.RSSModels;

/**
 * Created by zz on 2016/12/10.
 */
public class Tag implements Feed {
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