package com.stefanie20.ReadDay.Controllers;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.BufferedReader;

/**
 * Created by zz on 16/4/10.
 */
public class AddSubscriptionController {
    @FXML
    private TextField addressField;
    @FXML
    private Button addButton;
    @FXML
    private Label statusLabel;


    @FXML
    private void addButtonFired() {
        String address = addressField.getText();
        statusLabel.setText("Trying to add...");
        new Thread(()-> {
            BufferedReader reader = ConnectServer.connectServer(ConnectServer.addSubscriptionURL + address);
            Gson gson = new Gson();
            if (reader != null) {
                AddResult result = gson.fromJson(reader, AddResult.class);
                if (result.getNumResults() == 1) {
                    Platform.runLater(() -> statusLabel.setText("Added " + result.getStreamName()));
                } else {
                    Platform.runLater(() -> statusLabel.setText("Failed, is the address correct?"));
                }
            }
        }).start();

    }

}

class AddResult {
    private String query;
    private int numResults;
    private String streamId;
    private String streamName;

    public AddResult(String query, int numResults, String streamId, String streamName) {
        this.query = query;
        this.numResults = numResults;
        this.streamId = streamId;
        this.streamName = streamName;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getNumResults() {
        return numResults;
    }

    public void setNumResults(int numResults) {
        this.numResults = numResults;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public String getStreamName() {
        return streamName;
    }

    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }
}