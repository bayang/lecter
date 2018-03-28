package me.bayang.reader.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.dmfs.httpessentials.exceptions.ProtocolException;
import org.dmfs.oauth2.client.OAuth2AccessToken;
import org.dmfs.oauth2.client.OAuth2Scope;
import org.dmfs.oauth2.client.scope.StringScope;
import org.dmfs.rfc5545.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import me.bayang.reader.rssmodels.UserInformation;

@Service
public class PropertiesStorageServiceImpl implements IStorageService {
    
    private static final String userPropertiesFilename = "UserInfo.dat";
    private static final String tokenPropertiesFilename = "TokenInfo.dat";
    
    private static Logger LOGGER = LoggerFactory.getLogger(PropertiesStorageServiceImpl.class);
    
    @Override
    public void saveToken(OAuth2AccessToken token) throws FileNotFoundException, IOException, ProtocolException {
        Properties properties = new Properties();
        String accessToken = String.valueOf(token.accessToken());
        String refreshToken = String.valueOf(token.refreshToken());
        String scope = token.scope().toString();
        LOGGER.debug("access {}, refresh {}, scope {}", accessToken, refreshToken, scope);
        try {
            String accessTokenExpiration = token.expirationDate().toString();
            properties.setProperty("expiration", accessTokenExpiration);
            LOGGER.debug("expiration {}", accessTokenExpiration);
        } catch (ProtocolException e) {
            LOGGER.error("error reading token", e);
        }
        properties.setProperty("refresh", refreshToken);
        properties.setProperty("access", accessToken);
        properties.setProperty("scope", scope);
        properties.store(new FileOutputStream(new File(tokenPropertiesFilename)), null);
    }
    
    @Override
    public OAuth2AccessToken loadToken() {
        File file = new File(tokenPropertiesFilename);
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(file));
            String refreshToken = properties.getProperty("refresh");
            String accessToken = properties.getProperty("access");
            final String scope = properties.getProperty("scope");
            OAuth2AccessToken token = new OAuth2AccessToken() {
                public CharSequence accessToken() throws ProtocolException {
                    throw new UnsupportedOperationException("accessToken not present");
                }

                public CharSequence tokenType() throws ProtocolException {
                    throw new UnsupportedOperationException("tokenType not present");
                }

                public boolean hasRefreshToken() {
                   return true;
                }

                public CharSequence refreshToken() throws ProtocolException {
                   return refreshToken;
                }

                public DateTime expirationDate() throws ProtocolException {
                    throw new UnsupportedOperationException("expirationDate not present");
                }

                public OAuth2Scope scope() throws ProtocolException {
                    return new StringScope(scope);
                }
            };
            return token;
        } catch (IOException ioe) {
            LOGGER.error("error loading token", ioe);
            return null;
        }
    }
    
    @Override
    public String loadUser() {
        File file = new File(userPropertiesFilename);
        if (!file.exists()) {
            return null;
        }
        else {
            Properties properties = new Properties();
            try {
                properties.load(new FileInputStream(file));
                String userId = properties.getProperty("userId");
                return userId;
            } catch (IOException ioe) {
                LOGGER.error("error loading user", ioe);
            }
        }
        return null;
    }
    
    @Override
    public void saveUser(UserInformation user) {
        Properties properties = new Properties();
        properties.setProperty("userId", user.getUserId());
        try {
            properties.store(new FileOutputStream(userPropertiesFilename), null);
        } catch (IOException e) {
            LOGGER.error("error saving user", e);
        }
    }

    @Override
    public boolean hasToken() {
        File f = new File(tokenPropertiesFilename);
        return f.exists();
    }

    @Override
    public boolean hasUser() {
        File f = new File(userPropertiesFilename);
        return f.exists();
    }

    @Override
    public boolean prefersGridLayout() {
        return false;
    }

}
