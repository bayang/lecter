package me.bayang.reader.share.wallabag;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.di72nn.stuff.wallabag.apiwrapper.BasicParameterHandler;
import com.di72nn.stuff.wallabag.apiwrapper.WallabagService;
import com.di72nn.stuff.wallabag.apiwrapper.exceptions.UnsuccessfulResponseException;
import com.di72nn.stuff.wallabag.apiwrapper.models.Article;
import com.di72nn.stuff.wallabag.apiwrapper.models.Articles;
import com.di72nn.stuff.wallabag.apiwrapper.models.TokenResponse;

import me.bayang.reader.FXMain;
import me.bayang.reader.controllers.RssController;
import me.bayang.reader.storage.IStorageService;
import okhttp3.OkHttpClient;

@Service
public class WallabagClient {
    
    private static Logger LOGGER = LoggerFactory.getLogger(WallabagClient.class); 
    
    @Autowired
    private IStorageService storage;
    
    @Resource(name = "threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;
    
    private WallabagCredentials credentials;
    
    private OkHttpClient okHttpClient;
    
    private WallabagService wallabagService = null;
    
    @Resource(name="baseOkHttpClient")
    private OkHttpClient baseOkHttpClient;
    
    @PostConstruct
    public void initialize() {
        this.okHttpClient = baseOkHttpClient.newBuilder().build();
    }
    
    public boolean isConfigured() {
        WallabagCredentials credentials = storage.loadWallabagCredentials();
        if (! credentials.isValid()) {
            RssController.snackbarNotifyBlocking(FXMain.bundle.getString("wallabagLoginRequired"));
            return false;
        }
        else {
            this.setCredentials(credentials);
            initializeService();
            return true;
        }
    }
    
    public boolean testCredentials(WallabagCredentials credentials) {
        this.setCredentials(credentials);
        initializeService();
        try {
            Articles ar = this.getWallabagService().getArticlesBuilder().execute();
            storage.saveWallabagCredentials(getCredentials());
            return true;
        } catch (IOException | UnsuccessfulResponseException e) {
            LOGGER.error("error while testing wallabag credentials", e);
            // reset credentials and client
            this.setCredentials(null);
            this.setWallabagService(null);
            return false;
        }
    }
    
    @Async("threadPoolTaskExecutor")
    public void submitLink(String link, List<String> tags) {
        try {
            Article article = this.getWallabagService().addArticleBuilder(link).tags(tags).execute();
            LOGGER.debug("saved link '{}' to wallabag", article.url);
        } catch (IOException e) {
            LOGGER.error("error sending link to wallabag", e);
            RssController.snackbarNotify("error sending link to wallabag");
        } catch (UnsuccessfulResponseException e) {
            LOGGER.error("error sending link to wallabag, body {}", e.getResponseBody(), e);
            RssController.snackbarNotify("error sending link to wallabag");
        }
    }

    private void initializeService() {
        this.wallabagService = new WallabagService(this.getCredentials().getUrl(), 
                new BasicParameterHandler(
                this.getCredentials().getUsername(), this.getCredentials().getPassword(), this.getCredentials().getClientId(), this.getCredentials().getClientSecret(),
                this.getCredentials().getRefreshToken(), this.getCredentials().getAccessToken()) {
                    @Override
                    public boolean tokensUpdated(TokenResponse token) {
                            LOGGER.debug("wallabag token: " + token);
                            storage.saveWallabagRefreshToken(token.refreshToken);
                            getCredentials().setRefreshToken(token.refreshToken);
                            getCredentials().setAccessToken(token.accessToken);
                            return super.tokensUpdated(token);
                }
        }, this.okHttpClient);
        
    }

    public WallabagCredentials getCredentials() {
        return credentials;
    }

    public void setCredentials(WallabagCredentials credentials) {
        this.credentials = credentials;
    }

    public WallabagService getWallabagService() {
        return wallabagService;
    }

    public void setWallabagService(WallabagService wallabagService) {
        this.wallabagService = wallabagService;
    }
    
    
    

}
