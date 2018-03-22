package me.bayang.reader.rssmodels;

public class Origin{
    
    private String streamId;
    private String title;
    private String htmlUrl;

    public Origin() {
    }

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

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Origin [streamId=").append(streamId).append(", title=")
                .append(title).append(", htmlUrl=").append(htmlUrl).append("]");
        return builder.toString();
    }
    
    
}
