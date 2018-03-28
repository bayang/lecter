package me.bayang.reader.share.pocket;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PocketAddLinkPayload {
    
    private String url;
    
    private String title;
    
    private String tags;
    
    @JsonProperty("consumer_key")
    private String consumerKey;
    
    @JsonProperty("access_token")
    private String accessToken;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PocketAddLinkPayload [url=").append(url)
                .append(", title=").append(title).append(", tags=").append(tags)
                .append(", consumerKey=").append(consumerKey)
                .append(", accessToken=").append(accessToken).append("]");
        return builder.toString();
    }
    
    

}
