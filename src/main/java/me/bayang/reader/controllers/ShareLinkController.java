package me.bayang.reader.controllers;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import me.bayang.reader.FXMain;
import me.bayang.reader.components.DeletableLabel;
import me.bayang.reader.rssmodels.Item;
import me.bayang.reader.share.Provider;
import me.bayang.reader.share.pocket.PocketClient;
import me.bayang.reader.share.wallabag.WallabagClient;
import me.bayang.reader.view.ShareLinkView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

@FXMLController
public class ShareLinkController {
    
    private static Logger LOGGER  = LoggerFactory.getLogger(ShareLinkController.class);
    
    @Autowired
    private ShareLinkView view;
    
    @FXML
    private JFXTextArea linkField;
    
    @FXML
    private JFXTextField tagField;
    
    @FXML
    private JFXButton addTagButton;
    
    @FXML
    private FlowPane tagsContainer;
    
    @FXML
    private JFXButton submitButton;
    
    @Autowired
    private PocketClient pocketClient;
    
    @Autowired
    private WallabagClient wallabagClient;
    
    private Item currentItem;
    
    private Stage stage;
    
    private Provider currentProvider;
    
    @FXML
    public void initialize() {
        
    }
    
    private void submitToPocket() {
        Request r = pocketClient.addLink(linkField.getText(), formatTags());
        pocketClient.getHttpClient().newCall(r).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() != 200) {
                    LOGGER.debug("Pocket : non-200 code in onResponse : {} - message : {}", response.code(), response.header("X-Error"));
                    RssController.snackbarNotify("Message from Pocket server : " + response.header("X-Error"));
                }
                if (response.isSuccessful()) {
                    LOGGER.debug("successfully added link to Pocket");
                }
                try {
                    response.close();
                }
                catch (Exception e) {
                    /* noop */
                }
            }
            
            @Override
            public void onFailure(Call call, IOException e) {
                RssController.snackbarNotify("Error while sending link to Pocket");
                LOGGER.error("pocket onFailure in sending", e);
            }
        });
        this.stage.close();
    }
    
    private void submitToWallabag() {
        if (! tagsContainer.getChildren().isEmpty()) {
            List<String> tags = tagsContainer.getChildrenUnmodifiable().stream().map(l -> ((DeletableLabel) l).getContent()).collect(Collectors.toList());
            wallabagClient.submitLink(linkField.getText(), tags);
        }
        else {
            wallabagClient.submitLink(linkField.getText(), Collections.emptyList());
        }
        this.stage.close();
    }
    
    @FXML
    public void submitLink() {
        LOGGER.debug("link {}, tags {}", linkField.getText(), formatTags());
        if (this.getCurrentProvider() == Provider.POCKET) {
            submitToPocket();
        }
        else if (this.getCurrentProvider() == Provider.WALLABAG) {
            submitToWallabag();
        }
    }
    
    @FXML
    public void addTag() {
        if (! tagField.getText().isEmpty()) {
            DeletableLabel l = new DeletableLabel(tagField.getText());
            l.getDeleteCross().setOnMouseClicked(e -> {
                tagsContainer.getChildren().remove(l);
            });
            tagsContainer.getChildren().add(l);
            tagField.setText("");
        }
    }
    
    private String formatTags() {
        if (! tagsContainer.getChildren().isEmpty()) {
            return tagsContainer.getChildren().stream().map(l -> ((DeletableLabel) l).getContent()).collect(Collectors.joining(","));
        }
        else {
            return "";
        }
    }

    public Item getCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(Item currentItem) {
        this.currentItem = currentItem;
        this.linkField.setText(currentItem.getCanonical().get(0).getHref());
        this.tagField.setText("");
        this.tagsContainer.getChildren().clear();
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Provider getCurrentProvider() {
        return currentProvider;
    }

    public void setCurrentProvider(Provider currentProvider) {
        this.currentProvider = currentProvider;
        this.stage.setTitle(MessageFormat.format(FXMain.bundle.getString("shareLinkStageTitle"), currentProvider.toString()));
    }
    
    

}
