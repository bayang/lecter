package me.bayang.reader.share.pocket;

import com.fasterxml.jackson.annotation.JsonAlias;

public class PocketAccessTokenResponse {
    
    @JsonAlias({"access_token"})
    private String accessToken;
    
    private String username;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PocketAccessTokenResponse [accessToken=")
                .append(accessToken).append(", username=").append(username)
                .append("]");
        return builder.toString();
    }
    
    

}
