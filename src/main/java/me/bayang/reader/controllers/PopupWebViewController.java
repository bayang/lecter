package me.bayang.reader.controllers;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.event.HyperlinkEvent;

import org.codefx.libfx.control.webview.WebViewHyperlinkListener;
import org.codefx.libfx.control.webview.WebViews;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXRadioButton;

import de.felixroske.jfxsupport.FXMLController;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import me.bayang.reader.FXMain;
import me.bayang.reader.backend.inoreader.ConnectServer;
import me.bayang.reader.mobilizer.MercuryMobilizer;
import me.bayang.reader.mobilizer.MercuryResult;
import me.bayang.reader.rssmodels.Item;
import me.bayang.reader.share.pocket.PocketClient;
import me.bayang.reader.view.PocketAddLinkView;

@FXMLController
public class PopupWebViewController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PopupWebViewController.class);
    
    @FXML
    private WebView popupWebView;
    
    @FXML
    private JFXRadioButton popupRssRadioButton;
    
    @FXML
    private JFXRadioButton popupWebRadioButton;
    
    @FXML
    private JFXRadioButton mercuryRadioButton;
    
    @FXML
    private JFXProgressBar progressBar;
    
    @FXML
    private VBox container;
    
    @FXML
    private MenuItem pocketShareMenu;
    
    private WebViewHyperlinkListener eventPrintingListener;
    
    private AtomicBoolean isWebViewListenerAttached = new AtomicBoolean(false);
    
    private Item currentItem;
    
    private Stage stage;
    
    @Autowired
    private PocketAddLinkView pocketAddLinkView;
    
    @Autowired
    private MercuryMobilizer mercuryMobilizer;
    
    @Autowired
    private ConnectServer connectServer;
    
    @Autowired
    private PocketClient pocketClient;
    
    @FXML
    private void initialize() {
        initWebView();
        progressBar.setVisible(false);
        progressBar.prefWidthProperty().bind(container.widthProperty());
        ToggleGroup toggleGroup = new ToggleGroup();
        popupRssRadioButton.setToggleGroup(toggleGroup);
        popupWebRadioButton.setToggleGroup(toggleGroup);
        mercuryRadioButton.setToggleGroup(toggleGroup);
        popupRssRadioButton.setSelected(true);
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (toggleGroup.getSelectedToggle() != null) {
                if (popupRssRadioButton.isSelected()) {
                    popupWebView.getEngine().loadContent(Item.processContent(currentItem.getTitle(), currentItem.getSummary().getContent()));
                }
                else if (popupWebRadioButton.isSelected()) {
                    popupWebView.getEngine().load(currentItem.getCanonical().get(0).getHref());
                }
                else if (mercuryRadioButton.isSelected()) {
                    launchMercuryTask();
                }
            }
        });
    }
    
    private void initWebView() {
        popupWebView.setContextMenuEnabled(false);
        eventPrintingListener = event -> {
            LOGGER.debug("{}-{}",event.getURL(), event.getSource().getClass().getName());
            FXMain.getAppHostServices().showDocument(event.getURL().toString());
            return true;
        };
        popupWebView.getEngine().getLoadWorker().stateProperty().addListener((ObservableValue<? extends State> p, State oldState, State newState) -> {
            if (newState == State.SUCCEEDED) {
                popupWebView.setDisable(false);
                progressBar.setVisible(false);
                if (popupWebView.getEngine().getTitle() != null && ! popupWebView.getEngine().getTitle().isEmpty()) {
                    Platform.runLater(() -> {
                        stage.setTitle(popupWebView.getEngine().getTitle());
                    });
                }
                if (! isWebViewListenerAttached.get()) {
                    isWebViewListenerAttached.set(true);
                    WebViews.addHyperlinkListener(popupWebView, eventPrintingListener, HyperlinkEvent.EventType.ACTIVATED);
                }
            }
            else if (newState == State.SCHEDULED || newState == State.RUNNING) {
                popupWebView.setDisable(true);
                progressBar.setVisible(true);
            }
            else {
                popupWebView.setDisable(false);
                progressBar.setVisible(false);
            }
        });
    }
    
    @FXML
    public void shareItemPocket() {
        if (currentItem != null) {
            if (! pocketClient.isConfigured()) {
                return;
            }
            if (FXMain.pocketAddLinkStage == null) {
                FXMain.createPocketAddLinkStage();
                Scene scene = new Scene(pocketAddLinkView.getView());
                FXMain.pocketAddLinkStage.setScene(scene);
                FXMain.pocketAddLinkController = (PocketAddLinkController) pocketAddLinkView.getPresenter();
                FXMain.pocketAddLinkController.setStage(FXMain.pocketAddLinkStage);
                FXMain.pocketAddLinkController.setCurrentItem(currentItem);
                // Show the dialog and wait until the user closes it
                FXMain.pocketAddLinkStage.showAndWait();
            }
            else {
                FXMain.pocketAddLinkController.setCurrentItem(currentItem);
                // Show the dialog and wait until the user closes it
                FXMain.pocketAddLinkStage.showAndWait();
            }
        }
    }
    
    private void launchMercuryTask() {
        String href = this.currentItem.getCanonical().get(0).getHref();
        Task<MercuryResult> t = mercuryMobilizer.getMercuryResultTask(href);
        LOGGER.debug("{}", href);
        t.setOnSucceeded(e -> {
            progressBar.setVisible(false);
            MercuryResult m = t.getValue();
            Platform.runLater(() -> {
                stage.setTitle(m.getTitle());
            });
//            LOGGER.debug("{}",m);
            if (m != null && ! m.getContent().isEmpty()) {
                String url = m.getUrl();
                if (url.startsWith("/")) {
                    url = href;
                }
                popupWebView.getEngine().loadContent(MercuryMobilizer.formatContent(m.getImageUrl(), url, m.getContent()));
            }
        });
        connectServer.getTaskExecutor().submit(t);
        progressBar.setVisible(true);
    }

    public Item getCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(Item currentItem) {
        this.currentItem = currentItem;
        popupRssRadioButton.setSelected(true);
        popupWebView.getEngine().loadContent(Item.processContent(currentItem.getTitle(), currentItem.getSummary().getContent()));
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    

}
