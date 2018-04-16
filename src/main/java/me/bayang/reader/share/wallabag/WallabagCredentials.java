package me.bayang.reader.share.wallabag;

import org.apache.commons.lang3.StringUtils;

public class WallabagCredentials {
    
    private String url;
    
    private String username;
    
    private String password;
    
    private String clientId;
    
    private String clientSecret;
    
    private String refreshToken;
    
    private String accessToken;
    
    public WallabagCredentials() {
    }

    public WallabagCredentials(String url, String username, String password,
            String clientId, String clientSecret, String refreshToken,
            String accessToken) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
    }
    
    public boolean isValid() {
        return (! StringUtils.isBlank(this.getRefreshToken())
                && ! StringUtils.isBlank(this.getClientId())
                && ! StringUtils.isBlank(this.getClientSecret())
                && ! StringUtils.isBlank(this.getUrl())); 
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
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
        builder.append("WallabagCredentials [url=").append(url)
                .append(", username=").append(username).append(", password=")
                .append("****").append(", clientId=").append(clientId)
                .append(", clientSecret=").append(clientSecret)
                .append(", refreshToken=").append(refreshToken)
                .append(", accessToken=").append(accessToken).append("]");
        return builder.toString();
    }
    
}
