package me.bayang.reader.controllers;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

import org.apache.commons.text.StringEscapeUtils;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.textfield.CustomTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbar.SnackbarEvent;

import de.felixroske.jfxsupport.FXMLController;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import me.bayang.reader.FXMain;
import me.bayang.reader.backend.inoreader.ConnectServer;
import me.bayang.reader.rssmodels.Categories;
import me.bayang.reader.rssmodels.Feed;
import me.bayang.reader.rssmodels.FolderFeedOrder;
import me.bayang.reader.rssmodels.Item;
import me.bayang.reader.rssmodels.Readability;
import me.bayang.reader.rssmodels.Subscription;
import me.bayang.reader.rssmodels.Tag;
import me.bayang.reader.rssmodels.UserInfo;

/**
 * The RssController class for JavaFX application.
 */
@FXMLController
public class RssController {
    
    private static Logger LOGGER = LoggerFactory.getLogger(RssController.class);
    
    @FXML
    private BorderPane rssViewContainer;
    
    @FXML
    private TreeView<Feed> treeView;
    @FXML
    private Button refreshButton;
    @FXML
    private JFXListView<Item> listView;
    @FXML
    private WebView webView;
    @FXML
    private Button markReadButton;
    
    @FXML
    private FontAwesomeIconView plusIcon;
//    @FXML
//    private Label statusLabel;
    @FXML
    private RadioButton rssRadioButton;
    @FXML
    private RadioButton webRadioButton;
    @FXML
    private RadioButton readabilityRadioButton;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private CustomTextField searchBar;
    private ChangeListener<? super String> rssSearchListener;
    
    @Autowired
    private ConnectServer connectServer;
    
    @Autowired
    private FolderFeedOrder folderFeedOrder;
    
    @Autowired
    private OauthView oauthView;
    private OauthController oauthController;
    
    private Stage oauthDialogStage = null;
    private Stage addSubscriptionStage = null;
    private Stage editSubscriptionStage = null;
    
    @Autowired
    private AddSubscriptionView addSubscriptionView;
    private AddSubscriptionController addSubscriptionController;
    
    @Autowired
    private EditSubscriptionView editSubscriptionView;
    private EditSubscriptionController editSubscriptionController;

    private List<Item> itemList;
    private List<Item> readItemList = new ArrayList<>();
    private FilteredList<Item> filteredData;
    private SortedList<Item> sortedData;
    private List<Item> starredList;
    private Instant lastUpdateTime;
    private Task<TreeItem<Feed>> treeTask;
    private Task<List<Item>> itemListTask;
    private Task<List<Item>> starredListTask;
    private Task<List<Item>> olderItemsListTask;
    private Task<Map<String, Integer>> unreadCountsTask;
    private static Map<String, Integer> unreadCountsMap = new HashMap<>();
    private static TreeItem<Feed> root;
    
    private static JFXSnackbar snackbar;
    
    private ResourceBundle bundle = ResourceBundle.getBundle("i18n.translations");

    @FXML
    private void initialize() {
        FXMain.getStage().setMinWidth(600);
        FXMain.getStage().setMinHeight(600);
        eventHandleInitialize();
        radioButtonInitialize();
        initializeSearchBar();
        progressIndicator.setVisible(false);
        progressIndicator.progressProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.doubleValue() == 1.0) {
                    progressIndicator.setVisible(false);
                }
            }
        }));
       snackbar = new JFXSnackbar(rssViewContainer);
       plusIcon.setOnMouseEntered(event -> plusIcon.setFill(Color.CORNFLOWERBLUE));
       plusIcon.setOnMouseExited(event -> plusIcon.setFill(Color.BLACK));
       Tooltip t = new Tooltip(bundle.getString("loadRead"));
       Tooltip.install(plusIcon, t);
       plusIcon.setOnMouseClicked(e -> loadOlderReadArticles());
    }

    private void eventHandleInitialize() {
        listView.setCellFactory(l -> new ItemListCell(connectServer));
        treeView.setCellFactory(t -> new MyTreeCell());
        
        //handle event between listView and webView
        listView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getSummary().getContent() != null) {
                if (rssRadioButton.isSelected()) {
                    webView.getEngine().loadContent(Item.processContent(newValue.getTitle(), newValue.getSummary().getContent()));
                } else if (webRadioButton.isSelected()) {
                    webView.getEngine().load(newValue.getCanonical().get(0).getHref());
                } else if (readabilityRadioButton.isSelected()) {
                    new Thread(() -> {
                        String content = Readability.getReadabilityContent(newValue.getCanonical().get(0).getHref());
                        Platform.runLater(() -> webView.getEngine().loadContent(Item.processContent(newValue.getTitle(), content)));
                    }).start();
                }
                //send mark feed read to server if not in the starred list
                if (!treeView.getSelectionModel().getSelectedItem().getValue().getId().equals("user/" + UserInfo.getUserId() + "/state/com.google/starred")) {
                    if (!newValue.isRead()) {
                        connectServer.markAsRead(newValue.getDecimalId());
//                        new Thread(() -> connectServer.connectServer(ConnectServer.markFeedReadURL + newValue.getDecimalId())).start();

                        String streamId = newValue.getOrigin().getStreamId();
                        Integer count = unreadCountsMap.get(streamId);
                        unreadCountsMap.put(streamId, --count);

                        Integer allCount = unreadCountsMap.get("All Items");
                        unreadCountsMap.put("All Items", --allCount);

                        //set parent count
                        if (getParentItem(streamId) != null) {
                            String parent = getParentItem(streamId).getValue().getId();
                            Integer parentCount = unreadCountsMap.get(parent);
                            unreadCountsMap.put(parent, --parentCount);
                        }

                        LOGGER.debug("remove = {}", itemList.remove(newValue));
                        LOGGER.debug("add = {}", readItemList.add(newValue));
                        treeView.refresh();
                    }
                    newValue.setRead(true);//change state to read and change color in listView
                    listView.refresh();//force refresh, or it will take a while to show the difference
                }
            }
        }));
        //handle event between treeView and listView
        treeView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            List<Item> chosenItemList = new ArrayList<>();
            LOGGER.debug("itemList size {}, readItemList size {}", itemList.size(), readItemList.size());
            if (starredList != null && newValue != null) {
                if (newValue.getValue().getId().equals("user/" + UserInfo.getUserId() + "/state/com.google/starred")) {
                    chosenItemList = starredList;
                }
            }
            if (itemList != null && newValue != null) {
                if (newValue.getValue().getId().equals("All Items")) {//handle the special all items tag
                    chosenItemList = itemList;
                    chosenItemList.addAll(readItemList);
                }
                if (newValue.isLeaf()) {//leaf chosen list
                    for (Item item : itemList) {
                        if (newValue.getValue().getId().equals(item.getOrigin().getStreamId())) {
                            chosenItemList.add(item);
                        }
                    }
                    for (Item readItem : readItemList) {
                        if (newValue.getValue().getId().equals(readItem.getOrigin().getStreamId())) {
                            chosenItemList.add(readItem);
                        }
                    }
                } else {//handle the folder chosen list
                    for (Item item : itemList) {
                        for (String s : item.getCategories()) {
                            if (newValue.getValue().getId().equals(s)) {
                                chosenItemList.add(item);
                            }
                        }
                    }
                    for (Item readItem : readItemList) {
                        for (String s : readItem.getCategories()) {
                            if (newValue.getValue().getId().equals(s)) {
                                chosenItemList.add(readItem);
                            }
                        }
                    }
                }
            }
            
            filteredData = new FilteredList<>(FXCollections.observableArrayList(chosenItemList), p -> true);
            sortedData = new SortedList<>(filteredData, (o1, o2) -> (int) (Long.parseLong(o2.getCrawlTimeMsec()) - Long.parseLong(o1.getCrawlTimeMsec())));
//            chosenItemList.sort((o1, o2) -> (int) (Long.parseLong(o1.getCrawlTimeMsec()) - Long.parseLong(o2.getCrawlTimeMsec())));
//            listView.setItems(FXCollections.observableArrayList(chosenItemList));
            listView.setItems(sortedData);
        }));
    }

    private void radioButtonInitialize() {
        //set webView User Agent
        webView.getEngine().setUserAgent("Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36");

        //initialize the radio button
        ToggleGroup toggleGroup = new ToggleGroup();
        rssRadioButton.setToggleGroup(toggleGroup);
        webRadioButton.setToggleGroup(toggleGroup);
        readabilityRadioButton.setToggleGroup(toggleGroup);
        rssRadioButton.setSelected(true);
        toggleGroup.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {
            if (toggleGroup.getSelectedToggle() != null) {
                if (listView.getSelectionModel().getSelectedItem() != null) {
                    if (rssRadioButton.isSelected()) {
                        Item item = listView.getSelectionModel().getSelectedItem();
                        webView.getEngine().loadContent(Item.processContent(item.getTitle(), item.getSummary().getContent()));
                    } else if (webRadioButton.isSelected()) {
                        webView.getEngine().load(listView.getSelectionModel().getSelectedItem().getCanonical().get(0).getHref());
                    } else if (readabilityRadioButton.isSelected()) {
                        new Thread(() -> {
                            String content = Readability.getReadabilityContent(listView.getSelectionModel().getSelectedItem().getCanonical().get(0).getHref());
                            Platform.runLater(() -> webView.getEngine().loadContent(Item.processContent(listView.getSelectionModel().getSelectedItem().getTitle(), content)));
                        }).start();

                    }
                }
            }
        }));
    }
    
    private void initializeSearchBar() {
        FontAwesomeIconView searchIcon = new FontAwesomeIconView(FontAwesomeIcon.SEARCH, "15");
        searchBar.setLeft(searchIcon);
        rssSearchListener  = (obs, oldV, newV) -> {
            filteredData.setPredicate(item -> {
                // If filter text is empty, display all persons.
                if (filteredData.isEmpty()) {
                    return true;
                }
                if (newV == null || newV.isEmpty() || item == null) {
                    return true;
                }

                String lowerCaseFilter = newV.toLowerCase();

                if (item.toString().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                }
                return false; // Does not match.
            });
        };
        searchBar.textProperty().addListener(rssSearchListener);
    }

    private void taskInitialize() {
        //when get the treeItem from the URL, change the view
        treeTask = new Task<TreeItem<Feed>>() {
            @Override
            protected TreeItem<Feed> call() throws Exception {
                LOGGER.debug("start treeTask");
                return handleFolderFeedOrder();
            }
        };
        treeTask.setOnSucceeded(event -> {
            treeView.setRoot(treeTask.getValue());
            treeView.setShowRoot(false);
            progressIndicator.setProgress(progressIndicator.getProgress() + 0.25);
//            statusLabel.setText("Get Feeds Complete.");
            LOGGER.debug("finish treeTask");
        });

        //initialize itemList
        itemListTask = new Task<List<Item>>() {
            @Override
            protected List<Item> call() throws Exception {
                LOGGER.debug("start itemListTask");
                return connectServer.getStreamContent(ConnectServer.streamContentURL);
            }
        };
        itemListTask.setOnSucceeded(event -> {
            itemList = itemListTask.getValue();
            progressIndicator.setProgress(progressIndicator.getProgress() + 0.25);
//            statusLabel.setText("Get New Items Complete.");
            LOGGER.debug("finish itemListTask " + itemList.size());
        });
        //initialize starredList
        starredListTask = new Task<List<Item>>() {
            @Override
            protected List<Item> call() throws Exception {
                LOGGER.debug("start starredListTask");
                return connectServer.getStreamContent(ConnectServer.starredContentURL);
            }
        };
        starredListTask.setOnSucceeded(event -> {
            starredList = starredListTask.getValue();
            progressIndicator.setProgress(progressIndicator.getProgress() + 0.25);
//            statusLabel.setText("Get Starred Items Complete.");
            LOGGER.debug("finish starredListTask " + starredList.size());
        });
        //initialize unreadCountsMap
        unreadCountsTask = new Task<Map<String, Integer>>() {
            @Override
            protected Map<String, Integer> call() throws Exception {
                LOGGER.debug("start unreadCountsTask");
                return connectServer.getUnreadCountsMap();
            }
        };
        unreadCountsTask.setOnSucceeded(event -> {
            unreadCountsMap = unreadCountsTask.getValue();
            progressIndicator.setProgress(progressIndicator.getProgress() + 0.25);
//            statusLabel.setText("Get UnreadCounts Complete");
            LOGGER.debug("finish unreadCountsTask");
        });
    }

    private class MyTreeCell extends TreeCell<Feed> {
        private ContextMenu menu = new ContextMenu();

        public MyTreeCell() {
            MenuItem item = new MenuItem(bundle.getString("unsubscribe"));
            menu.getItems().add(item);
            item.setOnAction(event -> {
                TreeItem<Feed> sub = treeView.getSelectionModel().getSelectedItem();
                connectServer.unsubscribe(treeView.getSelectionModel().getSelectedItem().getValue().getId());
                //To clear the count, use markRead
                markReadButtonFired();
                sub.getParent().getChildren().remove(sub);
            });
            if (getItem() instanceof Subscription) {
                MenuItem editItem = new MenuItem(bundle.getString("editSubscription"));
                menu.getItems().add(editItem);
                editItem.setOnAction(event -> {
                    showEditSubscription((Subscription) getItem());
                });
            }
        }

        @Override
        protected void updateItem(Feed item, boolean empty) {//set the treeView style show title and icons
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                HBox hBox = new HBox();
                hBox.setSpacing(10);
                hBox.setMaxWidth(250);
                if (item instanceof Subscription) {
                    String title = StringEscapeUtils.unescapeHtml4(((Subscription) item).getTitle());
//                    LOGGER.debug("mytreecell instance {} {}", title, ((Subscription) item).getCategories());
                    Integer countInteger = unreadCountsMap.get(item.getId());
                    //create spaces to make counts in a row
                    Label countLabel = new Label(Objects.toString(countInteger, ""));
                    countLabel.getStyleClass().add("badge-label");
                    countLabel.setAlignment(Pos.CENTER);
                    countLabel.setPadding(new Insets(1.0));
                    Label titleLabel = new Label(title);
                    titleLabel.setPrefWidth(150);
                    Tooltip tooltip = new Tooltip(title);
                    this.setTooltip(tooltip);
                    //using HBox to add all the content
                    if (FolderFeedOrder.iconMap != null) {
                        ImageView imageView = new ImageView(FolderFeedOrder.iconMap.get(item.getId()));
                        imageView.setFitHeight(16);
                        imageView.setFitWidth(16);
                        hBox.getChildren().addAll(imageView, titleLabel, countLabel);
                    } else {
                        hBox.getChildren().addAll(titleLabel, countLabel);
                    }
                    MenuItem editItem = new MenuItem(bundle.getString("editSubscription"));
                    if (menu.getItems().size() > 1) {
                        menu.getItems().remove(1, menu.getItems().size());
                    }
                    menu.getItems().add(editItem);
                    editItem.setOnAction(event -> {
                        showEditSubscription((Subscription) item);
                    });
                    setContextMenu(menu);
                }
                else if (item instanceof Categories) {
                    String s = item.getId();
                    Integer countInteger = unreadCountsMap.getOrDefault(s, null);
                    String countString = Objects.toString(countInteger, "");
                    Label countLabel = new Label(countString);
                    countLabel.getStyleClass().add("badge-label");
                    countLabel.setAlignment(Pos.CENTER);
                    countLabel.setPadding(new Insets(1.0));
                    String label= StringEscapeUtils.unescapeHtml4(item.getLabel());
                    Label titleLabel = new Label(label);
                    titleLabel.setPrefWidth(120);
                    Tooltip tooltip = new Tooltip(label);
                    this.setTooltip(tooltip);

                    hBox.getChildren().addAll(titleLabel, countLabel);
                }
                else {
                    String s = item.getId();
                    Integer countInteger = unreadCountsMap.get(s);
                    String countString = Objects.toString(countInteger, "");
                    Label countLabel = new Label(countString);
                    Label titleLabel = new Label(s.substring(s.lastIndexOf("/") + 1));
                    titleLabel.setPrefWidth(120);

                    hBox.getChildren().addAll(titleLabel, countLabel);
                }
                setGraphic(hBox);
            }
        }
    }


    @FXML
    public void refreshFired() {
        // FIXME afficher popup correcte
        if (connectServer.isShouldAskPermissionOrLogin()) {
            new Alert(Alert.AlertType.ERROR, "Please Login.");
        }
        else {
            progressIndicator.setVisible(true);
            progressIndicator.setProgress(0);
//            statusLabel.setText("Refreshing...");
            taskInitialize();
            connectServer.getTaskExecutor().submit(unreadCountsTask);
            connectServer.getTaskExecutor().submit(treeTask);
            connectServer.getTaskExecutor().submit(itemListTask);
            connectServer.getTaskExecutor().submit(starredListTask);
            lastUpdateTime = Instant.now();
        }
    }

    @FXML
    private void markReadButtonFired() {
     // FIXME afficher popup correcte
        if (connectServer.isShouldAskPermissionOrLogin()) {
            new Alert(Alert.AlertType.ERROR, "Please Login.");
        } else {
            for (Item item : listView.getItems()) {
                LOGGER.debug("itemList size {}, readItemList size {}", itemList.size(), readItemList.size());
                if (! item.isRead()) {
                    item.setRead(true);
                    LOGGER.debug("remove = {}", itemList.remove(item));
                    LOGGER.debug("add = {}", readItemList.add(item));
                }
                LOGGER.debug("itemList size {}, readItemList size {}", itemList.size(), readItemList.size());
            }
            listView.refresh();
            //inform treeView to refresh the unread count
            if (!treeView.getSelectionModel().getSelectedItem().getValue().getId().equals("user/" + UserInfo.getUserId() + "/state/com.google/starred")) {
                Feed feed = treeView.getSelectionModel().getSelectedItem().getValue();
                if (feed instanceof Subscription) {
                    Integer count = unreadCountsMap.get(feed.getId());
                    unreadCountsMap.put(feed.getId(), 0);

                    Feed parent = treeView.getSelectionModel().getSelectedItem().getParent().getValue();
                    if (unreadCountsMap.get(parent.getId()) != null) {
                        unreadCountsMap.put(parent.getId(), unreadCountsMap.get(parent.getId()) - count);
                    }

                    unreadCountsMap.put("All Items", unreadCountsMap.get("All Items") - count);
                } else if (!feed.getId().equals("All Items")) {//parent treeItems, except All Items
                    Integer count = unreadCountsMap.get(feed.getId());
                    unreadCountsMap.put(feed.getId(), 0);
                    for (TreeItem<Feed> son : treeView.getSelectionModel().getSelectedItem().getChildren()) {
                        unreadCountsMap.put(son.getValue().getId(), 0);
                    }
                    unreadCountsMap.put("All Items", unreadCountsMap.get("All Items") - count);
                } else {//All Items
                    unreadCountsMap.put("All Items", 0);
                    root.getChildren().stream().filter(parent -> !parent.getValue().getId().equals("user/" + UserInfo.getUserId() + "/state/com.google/starred")).forEach(parent -> {
                        if (parent.getValue() instanceof Tag) {
                            for (TreeItem<Feed> son : parent.getChildren()) {
                                unreadCountsMap.put(son.getValue().getId(), 0);
                            }
                        }
                        unreadCountsMap.put(parent.getValue().getId(), 0);
                    });
                }
            }
            treeView.refresh();

            connectServer.markAllAsRead(lastUpdateTime.getEpochSecond(), treeView.getSelectionModel().getSelectedItem().getValue().getId());
//            new Thread(() -> {
//                connectServer.connectServer(ConnectServer.markAllReadURL + lastUpdateTime.getEpochSecond() + "&s=" + treeView.getSelectionModel().getSelectedItem().getValue().getId());
//            }).start();
        }
    }

    @FXML
    private void loginMenuFired() {
//        snackbarNotify("alert toto");
        // Create the dialog Stage.
        if (oauthDialogStage == null) {
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Login");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(FXMain.getStage());
            dialogStage.getIcons().add(new Image("icon.png"));
            dialogStage.setResizable(false);
            Scene scene = new Scene(oauthView.getView());
            dialogStage.setScene(scene);
            this.oauthDialogStage = dialogStage;
            oauthController = (OauthController) oauthView.getPresenter();
            oauthController.setStage(dialogStage);
            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();
        }
        else {
            oauthDialogStage.showAndWait();
        }
    }
    
    @FXML
    private void addSubscriptionFired() {
        if (addSubscriptionStage == null) {
            Stage dialogStage = new Stage();
            dialogStage.setTitle(bundle.getString("addSubscription"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(FXMain.getStage());
            dialogStage.getIcons().add(new Image("icon.png"));
            dialogStage.setResizable(false);
            Scene scene = new Scene(addSubscriptionView.getView());
            dialogStage.setScene(scene);
            this.addSubscriptionStage = dialogStage;
            addSubscriptionController = (AddSubscriptionController) addSubscriptionView.getPresenter();
            addSubscriptionController.setStage(dialogStage);
            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();
        }
        else {
            addSubscriptionStage.showAndWait();
        }
//        FXMain.getAddSubscriptionStage().show();
    }
    
    private void loadOlderReadArticles() {
        Item oldestItem = listView.getItems().get(listView.getItems().size() - 1);
        LOGGER.debug("{}",oldestItem.getOrigin().getStreamId());
//        connectServer.getOlderStreamContent(oldestItem.getOrigin().getStreamId());
        olderItemsListTask = connectServer.getOlderreadItemsTask(oldestItem.getOrigin().getStreamId());
//        List<Item> readItems = connectServer.getOlderStreamContent(oldestItem.getOrigin().getStreamId());
        olderItemsListTask.setOnSucceeded(event -> {
            readItemList.addAll(olderItemsListTask.getValue());
            listView.refresh();
            LOGGER.debug("finish loadOlderReadArticles " + olderItemsListTask.getValue().size());
        });
//        readItemList.addAll(readItems);
        connectServer.getTaskExecutor().submit(olderItemsListTask);
    }
    
    private void showEditSubscription(Subscription subscription) {
        if (editSubscriptionStage == null) {
            Stage dialogStage = new Stage();
            dialogStage.setTitle(bundle.getString("editSubscription"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(FXMain.getStage());
            dialogStage.getIcons().add(new Image("icon.png"));
            dialogStage.setResizable(true);
            Scene scene = new Scene(editSubscriptionView.getView());
            dialogStage.setScene(scene);
            this.editSubscriptionStage = dialogStage;
            editSubscriptionController = (EditSubscriptionController) editSubscriptionView.getPresenter();
            editSubscriptionController.setStage(dialogStage);
            editSubscriptionController.setSubscription(subscription);
            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();
        }
        else {
            editSubscriptionController.setSubscription(subscription);
            editSubscriptionStage.showAndWait();
        }
    }

    private TreeItem<Feed> handleFolderFeedOrder() {
        root = new TreeItem<>(new Tag("root", "root")); //the root node doesn't show;
        root.setExpanded(true);

        Map<Feed, List<Subscription>> map = folderFeedOrder.getAlphabeticalOrder();
        for (Feed feed : map.keySet()) {
            TreeItem<Feed> tag = new TreeItem<>(feed);
            if (map.get(feed) != null) {
                for (Subscription sub : map.get(feed)) {
                    TreeItem<Feed> subscription = new TreeItem<>(sub);
                    tag.getChildren().add(subscription);
                }
            }
            root.getChildren().add(tag);
        }
        return root;

    }

    private TreeItem<Feed> getParentItem(String streamId) {
        for (TreeItem<Feed> parent : root.getChildren()) {
            for (TreeItem<Feed> item : parent.getChildren()) {
                if (item.getValue().getId().equals(streamId)) {
                    return parent;
                }
            }
        }
        return null;
    }
    
    public static void notification() {
        Notifications notificationBuilder = Notifications.create()
                .title("Title Text")
                .text("alert §")
                .hideAfter(Duration.seconds(6))
                .position(Pos.TOP_RIGHT)
                .onAction(new javafx.event.EventHandler<ActionEvent>() {
                    
                    @Override public void handle(ActionEvent arg0) {
                        LOGGER.debug("Notification clicked on!");
                    }
                });
        notificationBuilder.show();
    }
    
    public static void snackbarNotify(String msg) {
        snackbar.enqueue(new SnackbarEvent(msg, null, 2500, false, null));
    }
}

