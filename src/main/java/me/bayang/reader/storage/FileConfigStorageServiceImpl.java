package me.bayang.reader.storage;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.dmfs.httpessentials.exceptions.ProtocolException;
import org.dmfs.oauth2.client.OAuth2AccessToken;
import org.dmfs.oauth2.client.OAuth2Scope;
import org.dmfs.oauth2.client.scope.StringScope;
import org.dmfs.rfc5545.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import me.bayang.reader.rssmodels.UserInformation;

@Service
@Primary
public class FileConfigStorageServiceImpl implements IStorageService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FileConfigStorageServiceImpl.class);
    
    @Resource(name="userConfig")
    private FileBasedConfigurationBuilder<FileBasedConfiguration> userConfig;
    
    @Resource(name="tokenConfig")
    private FileBasedConfigurationBuilder<FileBasedConfiguration> tokenConfig;
    
    @Resource(name="applicationConfig")
    private FileBasedConfigurationBuilder<FileBasedConfiguration> appConfig;
    
    private Configuration appConfiguration;
    private Configuration userConfiguration;
    private Configuration tokenConfiguration;
    
    @PostConstruct
    public void initialize() throws IOException, ConfigurationException {
        File propertiesFile = new File("config.properties");
        if (! propertiesFile.exists()) {
            propertiesFile.createNewFile();
        }
        File tokenPropertiesFile = new File("token.properties");
        if (! tokenPropertiesFile.exists()) {
            tokenPropertiesFile.createNewFile();
        }
        File userPropertiesFile = new File("user.properties");
        if (! userPropertiesFile.exists()) {
            userPropertiesFile.createNewFile();
        }
        this.appConfiguration = appConfig.getConfiguration();
        this.userConfiguration = userConfig.getConfiguration();
        this.tokenConfiguration = tokenConfig.getConfiguration();
        if (appConfiguration.getString("prefers.layout.grid") == null) {
            appConfiguration.setProperty("prefers.layout.grid", false);
        }
    }

    @Override
    public void saveToken(OAuth2AccessToken token) throws Exception {
        String accessToken = String.valueOf(token.accessToken());
        String refreshToken = String.valueOf(token.refreshToken());
        String scope = token.scope().toString();
        LOGGER.debug("access {}, refresh {}, scope {}", accessToken, refreshToken, scope);
        
        tokenConfiguration.setProperty("token.access", accessToken);
        tokenConfiguration.setProperty("token.refresh", refreshToken);
        tokenConfiguration.setProperty("token.scope", scope);
    }

    @Override
    public OAuth2AccessToken loadToken() {
        String refresh = tokenConfiguration.getString("token.refresh");
        String scope = tokenConfiguration.getString("token.scope");
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
               return refresh;
            }

            public DateTime expirationDate() throws ProtocolException {
                throw new UnsupportedOperationException("expirationDate not present");
            }

            public OAuth2Scope scope() throws ProtocolException {
                return new StringScope(scope);
            }
        };
        return token;  
    }

    @Override
    public void saveUser(UserInformation user) {
        userConfiguration.setProperty("userId", user.getUserId());
    }

    @Override
    public String loadUser() {
        return userConfiguration.getString("userId");
    }

    @Override
    public boolean hasToken() {
        return (tokenConfiguration != null 
                && tokenConfiguration.getString("token.refresh") != null 
                && tokenConfiguration.getString("token.scope") != null);
    }

    @Override
    public boolean hasUser() {
        return (userConfiguration != null && userConfiguration.getString("userId") != null);
    }
    
    @Override
    public boolean prefersGridLayout() {
        return appConfiguration.getBoolean("prefers.layout.grid", false);
    }

    public String getKey() {
        return this.appConfiguration.getString("toto", "default");
    }
    
    public void setKey() {
        this.appConfiguration.setProperty("toto", "code");
    }

}
