package me.bayang.reader.components;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.apache.commons.text.StringEscapeUtils;
import org.controlsfx.control.GridCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import me.bayang.reader.backend.inoreader.ConnectServer;
import me.bayang.reader.backend.inoreader.FolderFeedOrder;
import me.bayang.reader.controllers.RssController;
import me.bayang.reader.rssmodels.Item;
import me.bayang.reader.utils.StringUtils;

public class ItemGridCell extends GridCell<Item> {
    
private final Logger LOGGER = LoggerFactory.getLogger(getClass().getName());
    
    @FXML
    private VBox cellWrapper;
    
    @FXML
    private HBox firstLine;
    
    @FXML
    private Label fromLabel;

    @FXML
    private Label dateLabel;
    
    @FXML
    private Label subjectLabel;

    @FXML
    private Label contentLabel;
    
    @FXML
    private ImageView icon;
    
    private FXMLLoader mLLoader;
    
    private ContextMenu menu = new ContextMenu();
    
    private ConnectServer connectServer;
    
    private RssController rssController;
    
    private Item currentItem;
    
    private static PseudoClass READ_PSEUDO_CLASS = PseudoClass.getPseudoClass("read");
    
    BooleanProperty readProperty;

    public ItemGridCell(RssController rssController, ConnectServer connectServer) {
        super();
        this.rssController = rssController;
        this.connectServer = connectServer;
        this.getStyleClass().add("readable-cell");
        setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                this.rssController.showWebView(this.currentItem);
                this.rssController.markItemRead(this.currentItem);
            }
        });
        MenuItem starItem = new MenuItem("Mark Starred");
        MenuItem unStarItem = new MenuItem("Mark Unstarred");
        menu.getItems().addAll(starItem, unStarItem);

        starItem.setOnAction(event -> {
            LOGGER.debug("mark star " + this.currentItem.getDecimalId() + " "+this.currentItem.getSummary());
            this.connectServer.star(this.currentItem.getDecimalId());
            this.rssController.addToStarredList(currentItem);
        });
        unStarItem.setOnAction(event -> {
            LOGGER.debug("unstar " + this.currentItem.getDecimalId() + " "+this.currentItem.getSummary());
            this.connectServer.unStar(this.currentItem.getDecimalId());
            this.rssController.removeFromStarredList(currentItem);
        });
        this.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                e.consume();
            }
        });
        this.readProperty = new SimpleBooleanProperty(false);
        this.readProperty.addListener(e -> this.pseudoClassStateChanged(READ_PSEUDO_CLASS, readProperty.get()));
    }

    @Override
    protected void updateItem(Item item, boolean empty) {
        super.updateItem(item, empty);
        this.currentItem = item;
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(
                        getClass().getResource("/fxml/ItemGridCell.fxml"));
                mLLoader.setController(this);
                try {
                    mLLoader.load();
                } catch (IOException e) {
                    LOGGER.error("",e);
                }
            }
            readProperty.bind(item.readProperty());
            
            //get the time style
            LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(item.getCrawlTimeMsec())), ZoneId.systemDefault());
            String timeString = localDateTime.toLocalTime().format(DateTimeFormatter.ofPattern("kk:mm:ss"));
            if (!localDateTime.toLocalDate().equals(LocalDate.now())) {
                timeString = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
            }
//            LOGGER.debug("read {}", isReadProperty());
            fromLabel.setText(StringEscapeUtils.unescapeHtml4(item.getOrigin().getTitle()));
            dateLabel.setText(timeString);
            subjectLabel.setWrapText(true);
            subjectLabel.setText(StringEscapeUtils.unescapeHtml4(item.getTitle()));
            contentLabel.setText(StringUtils.processContent(item.getSummary().getContent()));
            
            setText(null);
            if (FolderFeedOrder.iconMap != null) {
                icon.setImage(FolderFeedOrder.iconMap.get(item.getOrigin().getStreamId()));
                icon.setFitWidth(20);
                icon.setFitHeight(20);
            }
            setGraphic(cellWrapper);
            setContextMenu(menu);
        }
    }
    
    public void setReadProperty(boolean read) {
        this.readProperty.set(read);
    }
    
    public boolean isReadProperty() {
        return this.readProperty.get();
    }
    
}
