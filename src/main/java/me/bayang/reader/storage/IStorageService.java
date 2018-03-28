package me.bayang.reader.storage;

import org.dmfs.oauth2.client.OAuth2AccessToken;

import me.bayang.reader.rssmodels.UserInformation;

public interface IStorageService {
    
    void saveToken(OAuth2AccessToken token) throws Exception;
    
    OAuth2AccessToken loadToken();
    
    void saveUser(UserInformation user);
    
    String loadUser();
    
    boolean hasToken();
    
    boolean hasUser();
    
    boolean prefersGridLayout();

}
