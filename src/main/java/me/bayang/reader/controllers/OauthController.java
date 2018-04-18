package me.bayang.reader.controllers;

import java.io.IOException;
import java.util.ResourceBundle;

import org.dmfs.httpessentials.exceptions.ProtocolError;
import org.dmfs.httpessentials.exceptions.ProtocolException;
import org.dmfs.oauth2.client.OAuth2AccessToken;
import org.dmfs.rfc3986.Uri;
import org.dmfs.rfc3986.encoding.Precoded;
import org.dmfs.rfc3986.uris.LazyUri;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import me.bayang.reader.backend.inoreader.ConnectServer;

@FXMLController
public class OauthController {
    
    private static Logger LOGGER = LoggerFactory.getLogger(OauthController.class);
    
    @FXML
    private WebView oauthView;
    
    @FXML
    private AnchorPane oauthViewWrapper;
    
    @Autowired
    private ConnectServer connectServer;
    
    private OAuth2AccessToken token;
    
    private Stage stage;
    
    private static ResourceBundle bundle = ResourceBundle.getBundle("i18n.translations");
    
    @FXML
    private void initialize() {
        
        try {
            oauthView.getEngine().load(connectServer.getAuthorizationUrl().toString());
        } catch (Exception ex) {
            RssController.snackbarNotify(bundle.getString("connectionFailure"));
            LOGGER.error("failure during initialization of oauthController", ex);
        }
            oauthView.getEngine().locationProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && newValue != null) {
                LOGGER.debug("{} -> {}",oldValue, newValue);
                if (newValue.contains("localhost:8080/reader/redirect")) {
                    Uri redirect = new LazyUri(new Precoded(newValue));
                    try {
                        token = connectServer.getGrant().withRedirect(redirect).accessToken(connectServer.getExecutor());
//                        LOGGER.debug("token : {} - {} - {} - {} - {}", token.accessToken(),token.expirationDate().toString(), token.refreshToken(), token.tokenType(), token.scope());
                        connectServer.setToken(token);
                        connectServer.setShouldAskPermissionOrLogin(false);
                        connectServer.fetchAndSaveUSer();
                        stage.close();
                    } catch (IOException e) {
                        LOGGER.error("failure during oauth token fetching and processing", e);
                    } catch (ProtocolError e) {
                        LOGGER.error("failure during oauth token fetching and processing", e);
                    } catch (ProtocolException e) {
                        LOGGER.error("failure during oauth token fetching and processing", e);
                    }
                }
                return;
            }
            if (oldValue != null) {
                LOGGER.debug(oldValue);
            }
            if (newValue != null) {
                LOGGER.debug(newValue);
            }
        });
    }

    public ConnectServer getConnectServer() {
        return connectServer;
    }

    public void setConnectServer(ConnectServer connectServer) {
        this.connectServer = connectServer;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
