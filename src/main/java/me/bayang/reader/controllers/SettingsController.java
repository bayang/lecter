package me.bayang.reader.controllers;

import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.jfoenix.controls.JFXSnackbar.SnackbarEvent;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLController;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import me.bayang.reader.FXMain;
import me.bayang.reader.share.wallabag.WallabagClient;
import me.bayang.reader.share.wallabag.WallabagCredentials;
import me.bayang.reader.storage.IStorageService;
import me.bayang.reader.utils.Theme;
import me.bayang.reader.view.PocketOauthView;
import me.bayang.reader.view.RssView;

@FXMLController
public class SettingsController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsController.class);
    
    private static ResourceBundle bundle = ResourceBundle.getBundle("i18n.translations");
    
    @Autowired
    private IStorageService configStorage;
    
    @FXML
    private VBox settingsContainer;
    
    @FXML
    private JFXToggleButton layoutToggle;
    
    @FXML
    private JFXToggleButton pocketActivate;
    
    @FXML
    private JFXToggleButton wallabagActivate;
    
    @FXML
    private JFXButton pocketConfigure;
    
    @FXML
    private JFXButton backButton;
    
    @FXML
    private Label pocketStatus;
    
    @FXML
    private JFXComboBox<String> themeComboBox;
    
    @FXML
    private JFXButton wallabagConnect;
    
    @FXML
    private JFXTextField wallabagUrlField;
    
    @FXML
    private JFXTextField wallabagUserField;
    
    @FXML
    private JFXPasswordField wallabagPasswordField;
    
    @FXML
    private JFXTextField wallabagClientIdField;
    
    @FXML
    private JFXTextField wallabagClientSecretField;
    
    @Autowired
    private PocketOauthView pocketOauthView;
    private PocketOauthController pocketOauthController;
    private Stage pocketOauthStage;
    
    private static JFXSnackbar snackbar;
    
    @Autowired
    public List<AbstractFxmlView> views;
    
    @Autowired
    private WallabagClient wallabagClient;
    
    @FXML
    public void initialize() {
        for (Theme t : Theme.values()) {
            themeComboBox.getItems().add(t.getDisplayName());
        }
        themeComboBox.getSelectionModel().select(configStorage.getAppTheme().getDisplayName());
        pocketActivate.selectedProperty().bindBidirectional(configStorage.pocketEnabledProperty());
        layoutToggle.selectedProperty().bindBidirectional(configStorage.prefersGridLayoutProperty());
        pocketStatus.textProperty().bind(Bindings.when(configStorage.pocketUserProperty().isEmpty())
                                .then("")
                                .otherwise(MessageFormat.format(bundle.getString("settingsShareProviderStatus"), configStorage.pocketUserProperty().getValue())));
        wallabagActivate.selectedProperty().bindBidirectional(configStorage.wallabagEnabledProperty());
        initWallabagFieldsBindings();
        snackbar = new JFXSnackbar(settingsContainer);
    }
    
    public void initWallabagFieldsBindings() {
        wallabagClientIdField.disableProperty().bind(Bindings.not(wallabagActivate.selectedProperty()));
        wallabagClientSecretField.disableProperty().bind(Bindings.not(wallabagActivate.selectedProperty()));
        wallabagUrlField.disableProperty().bind(Bindings.not(wallabagActivate.selectedProperty()));
        wallabagPasswordField.disableProperty().bind(Bindings.not(wallabagActivate.selectedProperty()));
        wallabagUserField.disableProperty().bind(Bindings.not(wallabagActivate.selectedProperty()));
    }
    
    @FXML
    public void showMainScreen() {
        FXMain.showView(RssView.class);
    }
    
    @FXML
    public void showPocketOauthStage() {
        if (pocketOauthStage == null) {
            Stage dialogStage = new Stage();
            dialogStage.setTitle(bundle.getString("pocketLogin"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(FXMain.getStage());
            dialogStage.getIcons().add(new Image("icon.png"));
            dialogStage.setResizable(true);
            Scene scene = new Scene(pocketOauthView.getView());
            dialogStage.setScene(scene);
            this.pocketOauthStage = dialogStage;
            pocketOauthController = (PocketOauthController) pocketOauthView.getPresenter();
            pocketOauthController.setStage(dialogStage);
            // Show the dialog and wait until the user closes it
            pocketOauthController.processLogin();
            dialogStage.showAndWait();
        }
        else {
            pocketOauthController.processLogin();
            pocketOauthStage.showAndWait();
        }
    }
    
    @FXML
    public void changeTheme() {
        LOGGER.debug("theme : {}", themeComboBox.getValue());
        Theme theme = Theme.forDisplayName(themeComboBox.getValue());
        if (theme != null) {
            configStorage.setAppTheme(theme);
            setTheme(theme);
        }
    }
    
    public void setTheme(Theme theme) {
        LOGGER.debug("changing theme to {}", theme.getPath());
        FXMain.getScene().getStylesheets().clear();
        FXMain.setUserAgentStylesheet(null);
        FXMain.setUserAgentStylesheet(FXMain.STYLESHEET_MODENA);
        FXMain.getScene().getStylesheets().add(getClass().getResource(theme.getPath()).toExternalForm());
        
        for (AbstractFxmlView v : views) {
            v.getView().getStylesheets().clear();
            v.getView();
        }
    }
    
    @FXML
    public void wallabagConnect() {
        if (validateWallabagFields()) {
            WallabagCredentials credentials = 
                    new WallabagCredentials(wallabagUrlField.getText(), wallabagUserField.getText(), 
                            wallabagPasswordField.getText(), wallabagClientIdField.getText(), 
                            wallabagClientSecretField.getText(), null, null);
            if (wallabagClient.testCredentials(credentials)) {
                snackbarNotify(FXMain.bundle.getString("reachedServer"), 2000);
            }
            else {
                snackbarNotify(FXMain.bundle.getString("failedVerification"), 2500);
            }
        }
        else {
            snackbarNotify(FXMain.bundle.getString("allFieldsMandatory"), 2000);
        }
    }
    
    private boolean validateWallabagFields() {
        if (!StringUtils.isBlank(wallabagClientIdField.getText())
                && ! StringUtils.isBlank(wallabagClientSecretField.getText())
                && ! StringUtils.isBlank(wallabagPasswordField.getText())
                && ! StringUtils.isBlank(wallabagUserField.getText())
                && ! StringUtils.isBlank(wallabagUrlField.getText())) {
            return true;
        }
        return false;
    }

    public static void snackbarNotify(String msg, long duration) {
        if (msg != null) {
            snackbar.enqueue(new SnackbarEvent(msg, null, duration, false, null));
        }
    }

}
