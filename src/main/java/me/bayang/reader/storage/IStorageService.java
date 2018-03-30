package me.bayang.reader.storage;

import org.dmfs.oauth2.client.OAuth2AccessToken;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import me.bayang.reader.rssmodels.UserInformation;

public interface IStorageService {
    
    void saveToken(OAuth2AccessToken token) throws Exception;
    
    void savePocketToken(String token);
    
    String loadPocketToken();
    
    void setPocketUser(String user);
    
    StringProperty pocketUserProperty();
    
    OAuth2AccessToken loadToken();
    
    void saveUser(UserInformation user);
    
    String loadUser();
    
    boolean hasToken();
    
    boolean hasUser();
    
    boolean prefersGridLayout();
    
    public BooleanProperty pocketEnabledProperty();
    
    public BooleanProperty prefersGridLayoutProperty();

}
