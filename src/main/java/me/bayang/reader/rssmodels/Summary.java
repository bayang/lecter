package me.bayang.reader.rssmodels;

/**
 * Created by zz on 2016/12/10.
 */
public class Summary{
    
    private String direction;
    private String content;

    public Summary() {
    }

    public Summary(String direction, String content) {
        this.direction = direction;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Summary [direction=").append(direction)
                .append(", content=").append(content).append("]");
        return builder.toString();
    }
    
    
    
}
