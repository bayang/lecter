package me.bayang.reader.controllers;

import org.springframework.beans.factory.annotation.Autowired;

import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import me.bayang.reader.config.AppConfig;
import me.bayang.reader.utils.StringUtils;

@FXMLController
public class AboutPopupController {
    
    @FXML
    private ImageView appLogo;
    
    @FXML
    private Hyperlink hyperLink;
    
    @FXML
    private Label versionLabel;
    
    @Autowired
    private AppConfig appConfig;
    
    private Stage stage;
    
    @FXML
    public void initialize() {
        versionLabel.setText("Version : " + appConfig.appVersion);
    }
    
    @FXML
    public void openLink() {
        StringUtils.openHyperlink(hyperLink.getText());
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    

}
