package me.bayang.reader.rssmodels;

/**
 * Created by zz on 2016/12/10.
 */
public class Tag implements Feed {
    
    private String id;
    private String sortid;

    public Tag() {
    }

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
    public String getLabel() {
        return getId();
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSortid(String sortid) {
        this.sortid = sortid;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Tag [id=").append(id).append(", sortid=").append(sortid)
                .append("]");
        return builder.toString();
    }

}