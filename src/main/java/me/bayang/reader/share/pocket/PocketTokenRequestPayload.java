package me.bayang.reader.share.pocket;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PocketTokenRequestPayload {
    
    @JsonProperty("consumer_key")
    private String consumerKey;
    
    @JsonProperty("redirect_uri")
    private String redirectUri;

    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PocketTokenRequestPayload [consumerKey=")
                .append(consumerKey).append(", redirectUri=")
                .append(redirectUri).append("]");
        return builder.toString();
    }
    
    

}
