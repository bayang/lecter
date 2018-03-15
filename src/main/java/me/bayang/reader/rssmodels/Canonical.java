package me.bayang.reader.rssmodels;

/**
 * Created by zz on 2016/12/10.
 */
public class Canonical{
    
    private String href;
    
    public Canonical() {
    }

    public Canonical(String href) {
        this.href = href;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Canonical [href=").append(href).append("]");
        return builder.toString();
    }
    
    
}
