package me.bayang.reader.share.pocket;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PocketAccessTokenPayload {
    
    @JsonProperty("consumer_key")
    private String consumerKey;
    
    private String code;

    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PocketAccessTokenPayload [consumerKey=")
                .append(consumerKey).append(", code=").append(code).append("]");
        return builder.toString();
    }
    
    
    
}
