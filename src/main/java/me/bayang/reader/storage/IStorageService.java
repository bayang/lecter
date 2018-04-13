package me.bayang.reader.storage;

import org.dmfs.oauth2.client.OAuth2AccessToken;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import me.bayang.reader.rssmodels.UserInformation;
import me.bayang.reader.share.wallabag.WallabagCredentials;
import me.bayang.reader.utils.Theme;

public interface IStorageService {
    
    void saveToken(OAuth2AccessToken token) throws Exception;
    
    void savePocketToken(String token);
    
    String loadPocketToken();
    
    WallabagCredentials loadWallabagCredentials();
    
    void saveWallabagCredentials(WallabagCredentials wallabagCredentials);
    
    void saveWallabagRefreshToken(String token);
    
    void setPocketUser(String user);
    
    StringProperty pocketUserProperty();
    
    OAuth2AccessToken loadToken();
    
    void saveUser(UserInformation user);
    
    String loadUser();
    
    boolean hasToken();
    
    boolean hasUser();
    
    boolean prefersGridLayout();
    
    BooleanProperty pocketEnabledProperty();
    
    BooleanProperty wallabagEnabledProperty();
    
    BooleanProperty prefersGridLayoutProperty();
    
    Theme getAppTheme();

    void setAppTheme(Theme appTheme);

}
