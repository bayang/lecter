package me.bayang.reader.controllers;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import me.bayang.reader.share.pocket.PocketAccessTokenPayload;
import me.bayang.reader.share.pocket.PocketAccessTokenResponse;
import me.bayang.reader.share.pocket.PocketClient;
import me.bayang.reader.share.pocket.PocketTokenResponse;
import okhttp3.Response;

@FXMLController
public class PocketOauthController {
    
    private static Logger LOGGER = LoggerFactory.getLogger(PocketOauthController.class);
    
    @FXML
    private WebView oauthView;
    
    @FXML
    private AnchorPane oauthViewWrapper;
    
    @Autowired
    private PocketClient pocketClient;
    
    @Resource(name="mapper")
    private ObjectMapper mapper;
    
    private Stage stage;
    
    private static ResourceBundle bundle = ResourceBundle.getBundle("i18n.translations");
    
    public void processLogin() {
        Response response = null;
        try {
            response = pocketClient.getRequestToken();
            if (response.isSuccessful()) {
                PocketTokenResponse pr = mapper.readValue(response.body().charStream(), PocketTokenResponse.class);
                LOGGER.debug("pocketrequesttoken {}", pr);
                pocketClient.setCode(pr.getCode());
                String url = pocketClient.loginApprovalUrl(pr.getCode());
                LOGGER.debug("url {}", url);
                oauthView.getEngine().load(url);
            }
            else {
                RssController.snackbarNotify(bundle.getString("connectionFailure"));
                LOGGER.error("error header : {}", response.header("X-Error"));
                LOGGER.error("failure during initialization of PocketOauthController code {}", response.code());
            }
        } catch (Exception ex) {
            RssController.snackbarNotify(bundle.getString("connectionFailure"));
            LOGGER.error("failure during initialization of oauthController", ex);
        }
        finally {
            try {
                response.close();
            }
            catch (Exception e) {
                /* noop */
            }
        }
    }
    
    @FXML
    private void initialize() {
        oauthView.getEngine().locationProperty().addListener((observable, oldValue, newValue) -> {
//            if (pocketClient.getCode() == null || pocketClient.getCode().isEmpty()) {
//                return;
//            }
            if (oldValue != null && newValue != null) {
                LOGGER.debug("{} -> {}",oldValue, newValue);
                if (newValue.contains(PocketClient.redirectUrlNoScheme)) {
                    PocketAccessTokenPayload accessTokenPayload = pocketClient.getAccessTokenPayload();
                    if (accessTokenPayload == null) {
                        RssController.snackbarNotify("missing informations or credentials");
                    }
                    LOGGER.debug("pocketaccesstokenpayload {}", accessTokenPayload);
                    Response accessResponse = pocketClient.getAccessToken(accessTokenPayload);
                    if (accessResponse.isSuccessful()) {
                        try {
                            PocketAccessTokenResponse re = mapper.readValue(accessResponse.body().charStream(), PocketAccessTokenResponse.class);
                            LOGGER.debug("pocketaccesstokenresponse {}", re);
                            pocketClient.setAccessToken(re.getAccessToken());
                            pocketClient.setUsername(re.getUsername());
                            accessResponse.close();
                            stage.close();
                        } catch (IOException e) {
                            accessResponse.close();
                            LOGGER.error("error processing server response",e);
                        }
                    }
                    else {
                        RssController.snackbarNotify(bundle.getString("connectionFailure"));
                        LOGGER.error("error header : {}", accessResponse.header("X-Error"));
                        LOGGER.error("failure during initialization of PocketOauthController code {}", accessResponse.code());
                        accessResponse.close();
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

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
