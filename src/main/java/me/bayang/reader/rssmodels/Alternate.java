package me.bayang.reader.rssmodels;

public class Alternate{
    private String href;
    private String type;
    
    public Alternate() {
    }

    public Alternate(String href, String type) {
        this.href = href;
        this.type = type;
    }

    public String getHref() {
        return href;
    }

    public String getType() {
        return type;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Alternate [href=").append(href).append(", type=")
                .append(type).append("]");
        return builder.toString();
    }
    
    
}