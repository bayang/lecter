package me.bayang.reader.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.felixroske.jfxsupport.FXMLController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import me.bayang.reader.backend.inoreader.ConnectServer;
import me.bayang.reader.rssmodels.AddResult;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by zz on 16/4/10.
 */
@FXMLController
public class AddSubscriptionController {
    @FXML
    private TextField addressField;
    @FXML
    private Button addButton;
    @FXML
    private Label statusLabel;

    @Autowired
    ConnectServer connectServer;
    
    @Autowired
    private ObjectMapper mapper;
    
    private ResourceBundle bundle = ResourceBundle.getBundle("i18n.translations");
    
    private Stage stage;

    @FXML
    private void addButtonFired() {
        String address = addressField.getText();
        statusLabel.setText(bundle.getString("addSubscriptionOngoing"));
        connectServer.getOkClient().newCall(connectServer.getRequest(ConnectServer.addSubscriptionURL + address)).enqueue(new Callback() {
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().source().inputStream(), "utf-8"));
                AddResult result = mapper.readValue(reader, AddResult.class);
                if (result.getNumResults() == 1) {
                    Platform.runLater(() -> statusLabel.setText(MessageFormat.format(bundle.getString("successAddSubscription"), result.getStreamName())));
                } else {
                    Platform.runLater(() -> statusLabel.setText(bundle.getString("failedAddSubscription")));
                }
                ConnectServer.closeReader(reader);
            }
            
            @Override
            public void onFailure(Call arg0, IOException arg1) {
                Platform.runLater(() -> statusLabel.setText(bundle.getString("failedAddSubscriptionNetwork")));
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