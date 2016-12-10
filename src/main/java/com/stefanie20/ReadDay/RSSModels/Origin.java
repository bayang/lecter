package com.stefanie20.ReadDay.RSSModels;

/**
 * Created by zz on 2016/12/10.
 */
public class Origin{
    private String streamId;
    private String title;
    private String htmlUrl;

    public Origin(String streamId, String title, String htmlUrl) {
        this.streamId = streamId;
        this.title = title;
        this.htmlUrl = htmlUrl;
    }

    public String getStreamId() {
        return streamId;
    }

    public String getTitle() {
        return title;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }
}
