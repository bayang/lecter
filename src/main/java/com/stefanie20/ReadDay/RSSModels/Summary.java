package com.stefanie20.ReadDay.RSSModels;

/**
 * Created by zz on 2016/12/10.
 */
public class Summary{
    private String direction;
    private String content;

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
}
