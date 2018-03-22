package me.bayang.reader.controllers;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.event.HyperlinkEvent;

import org.codefx.libfx.control.webview.WebViewHyperlinkListener;
import org.codefx.libfx.control.webview.WebViews;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXRadioButton;

import de.felixroske.jfxsupport.FXMLController;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import me.bayang.reader.FXMain;
import me.bayang.reader.rssmodels.Item;

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
    private JFXProgressBar progressBar;
    
    @FXML
    private VBox container;
    
    private WebViewHyperlinkListener eventPrintingListener;
    
    private AtomicBoolean isWebViewListenerAttached = new AtomicBoolean(false);
    
    private Item currentItem;
    
    private Stage stage;
    
    @FXML
    private void initialize() {
        initWebView();
        progressBar.setVisible(false);
        progressBar.prefWidthProperty().bind(container.widthProperty());
        ToggleGroup toggleGroup = new ToggleGroup();
        popupRssRadioButton.setToggleGroup(toggleGroup);
        popupWebRadioButton.setToggleGroup(toggleGroup);
        popupRssRadioButton.setSelected(true);
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (toggleGroup.getSelectedToggle() != null) {
                if (popupRssRadioButton.isSelected()) {
                    popupWebView.getEngine().loadContent(Item.processContent(currentItem.getTitle(), currentItem.getSummary().getContent()));
                }
                else if (popupWebRadioButton.isSelected()) {
                    popupWebView.getEngine().load(currentItem.getCanonical().get(0).getHref());
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
                stage.setTitle(popupWebView.getEngine().getTitle());
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
