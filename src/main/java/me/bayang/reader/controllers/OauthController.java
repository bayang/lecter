package me.bayang.reader.controllers;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.dmfs.httpessentials.client.HttpRequestExecutor;
import org.dmfs.httpessentials.exceptions.ProtocolError;
import org.dmfs.httpessentials.exceptions.ProtocolException;
import org.dmfs.httpessentials.okhttp.OkHttpExecutor;
import org.dmfs.jems.single.Single;
import org.dmfs.jems.single.elementary.ValueSingle;
import org.dmfs.oauth2.client.BasicOAuth2AuthorizationProvider;
import org.dmfs.oauth2.client.BasicOAuth2Client;
import org.dmfs.oauth2.client.BasicOAuth2ClientCredentials;
import org.dmfs.oauth2.client.OAuth2AccessToken;
import org.dmfs.oauth2.client.OAuth2AuthorizationProvider;
import org.dmfs.oauth2.client.OAuth2Client;
import org.dmfs.oauth2.client.OAuth2ClientCredentials;
import org.dmfs.oauth2.client.OAuth2InteractiveGrant;
import org.dmfs.oauth2.client.grants.AuthorizationCodeGrant;
import org.dmfs.oauth2.client.scope.BasicScope;
import org.dmfs.rfc3986.Uri;
import org.dmfs.rfc3986.encoding.Precoded;
import org.dmfs.rfc3986.uris.LazyUri;
import org.dmfs.rfc5545.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import me.bayang.reader.FXMain;
import me.bayang.reader.backend.inoreader.ConnectServer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@FXMLController
public class OauthController {
    
    private static Logger LOGGER = LoggerFactory.getLogger(OauthController.class);
    
    @FXML
    WebView oauthView;
    
    @FXML
    AnchorPane oauthViewWrapper;
    
    @Autowired
    private ConnectServer connectServer;
    
    private OAuth2AccessToken token;
    
    private Stage stage;
    
    @FXML
    private void initialize() {
        oauthView.getEngine().load(connectServer.getAuthorizationUrl().toString());
//        oauthView.getEngine().loadContent("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>Insert title here</title></head><body>    <p>TEST TESTETS <a href=\"http://localhost:8080/reader/redirect\">LOCALHOST</a></p></body></html>");        
            oauthView.getEngine().locationProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && newValue != null) {
                LOGGER.debug("{} -> {}",oldValue, newValue);
                if (newValue.contains("localhost:8080/reader/redirect")) {
                    Uri redirect = new LazyUri(new Precoded(newValue));
                    try {
                        token = connectServer.getGrant().withRedirect(redirect).accessToken(connectServer.getExecutor());
                        LOGGER.debug("token : {} - {} - {} - {} - {}", token.accessToken(),token.expirationDate().toString(), token.refreshToken(), token.tokenType(), token.scope());
                        connectServer.setToken(token);
                        connectServer.fetchAndSaveUSer();
//                        String authorization = String.format("Bearer %s", token.accessToken());
//                        Request request = new Request.Builder()
//                                .url("https://www.inoreader.com/reader/api/0/user-info")
//                                .addHeader("Authorization", authorization)
//                                .build();
//                        Response response = connectServer.getOkClient().newCall(request).execute();
//                        LOGGER.debug(response.body().string());
                        stage.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (ProtocolError e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (ProtocolException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
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
