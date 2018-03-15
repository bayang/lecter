package me.bayang.reader.rssmodels;

public class Categories implements Feed {
    private String id;
    private String label;

    public Categories() {
    }

    public Categories(String id, String label) {
        this.id = id;
        this.label = label;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getSortid() {
        return null;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Categories [id=").append(id).append(", label=")
                .append(label).append("]");
        return builder.toString();
    }
    
    
}