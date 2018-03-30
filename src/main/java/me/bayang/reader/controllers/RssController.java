package me.bayang.reader.controllers;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

import javax.swing.event.HyperlinkEvent;

import org.apache.commons.text.StringEscapeUtils;
import org.codefx.libfx.control.webview.WebViewHyperlinkListener;
import org.codefx.libfx.control.webview.WebViews;
import org.controlsfx.control.GridView;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.textfield.CustomTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbar.SnackbarEvent;

import de.felixroske.jfxsupport.FXMLController;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import eu.lestard.advanced_bindings.api.CollectionBindings;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import me.bayang.reader.FXMain;
import me.bayang.reader.backend.UserInfo;
import me.bayang.reader.backend.inoreader.ConnectServer;
import me.bayang.reader.backend.inoreader.FolderFeedOrder;
import me.bayang.reader.components.ItemGridCell;
import me.bayang.reader.components.ItemListCell;
import me.bayang.reader.mobilizer.MercuryMobilizer;
import me.bayang.reader.mobilizer.MercuryResult;
import me.bayang.reader.rssmodels.Categories;
import me.bayang.reader.rssmodels.Feed;
import me.bayang.reader.rssmodels.Item;
import me.bayang.reader.rssmodels.Subscription;
import me.bayang.reader.rssmodels.Tag;
import me.bayang.reader.share.pocket.PocketClient;
import me.bayang.reader.storage.IStorageService;
import me.bayang.reader.view.AboutPopupView;
import me.bayang.reader.view.AddSubscriptionView;
import me.bayang.reader.view.EditSubscriptionView;
import me.bayang.reader.view.OauthView;
import me.bayang.reader.view.PocketAddLinkView;
import me.bayang.reader.view.PopupWebView;
import me.bayang.reader.view.SettingsView;

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
    private Button switchViewButton;
    @FXML
    private Button addSubscriptionButton;
    @FXML
    private FontAwesomeIconView switchViewIcon;
    @FXML
    private FontAwesomeIconView plusIcon;
    @FXML
    private FontAwesomeIconView plusIconGrid;
    @FXML
    private JFXRadioButton rssRadioButton;
    @FXML
    private JFXRadioButton webRadioButton;
    @FXML
    private JFXRadioButton mercuryRadioButton;
    @FXML
    private JFXProgressBar progressBar;
    @FXML
    private JFXProgressBar webViewProgressBar;
    @FXML
    private VBox toolBarContainer;
    @FXML
    private VBox listViewContainer;
    @FXML
    private VBox webViewContainer;
    @FXML
    private GridView<Item> gridView;
    @FXML
    private VBox gridContainer;
    @FXML
    private SplitPane splitPane;
    
    @FXML
    private MenuItem pocketShareMenu;
    
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
    private Stage popupWebViewStage = null;
    private Stage aboutPopupStage = null;
    
    @Autowired
    private AddSubscriptionView addSubscriptionView;
    private AddSubscriptionController addSubscriptionController;
    
    @Autowired
    private EditSubscriptionView editSubscriptionView;
    private EditSubscriptionController editSubscriptionController;

    @Autowired
    private PopupWebView popupWebView;
    private PopupWebViewController popupWebViewController;
    
    @Autowired
    private AboutPopupView aboutPopupView;
    private AboutPopupController aboutPopupController;
    
    @Autowired
    private PocketAddLinkView pocketAddLinkView;
    private PocketAddLinkController pocketAddLinkController;
    
    private List<Item> itemList = new ArrayList<>();
    private List<Item> readItemList = new ArrayList<>();
    private ObservableList<Item> observableItemList = FXCollections.observableArrayList(itemList);
    private ObservableList<Item> observableReadList = FXCollections.observableArrayList(readItemList);
    // FIXME find a less greedy data structure
    private ObservableList<Item> observableAllList = CollectionBindings.concat(observableItemList, observableReadList);
    
    private FilteredList<Item> filteredData;
    private Predicate <? super Item> currentPredicate;
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
    
    private static ResourceBundle bundle = ResourceBundle.getBundle("i18n.translations");
    
    private BooleanProperty gridModeproperty = new SimpleBooleanProperty(false);
    
    private final FontAwesomeIconView listIcon = new FontAwesomeIconView(FontAwesomeIcon.BARS);
    private final FontAwesomeIconView gridIcon = new FontAwesomeIconView(FontAwesomeIcon.TH);
    
    private WebViewHyperlinkListener eventPrintingListener;
    
    private AtomicBoolean isWebViewListenerAttached = new AtomicBoolean(false);
    
    @Autowired
    private MercuryMobilizer mercuryMobilizer;
    
    @Autowired
    private PocketClient pocketClient;
    
    @Autowired
    private IStorageService configStorage;
    
    private Item currentlySelectedItem = null;
    
    @FXML
    private void initialize() {
        FXMain.getStage().setMinWidth(700);
        FXMain.getStage().setMinHeight(650);
        treeView.setDisable(true);
        splitPane.getItems().remove(gridContainer);
        eventHandleInitialize();
        initializeRadioButton();
        initializeSearchBar();
        initializeProgressBar();
        listIcon.setFill(Color.valueOf("#a5a3a3"));
        listIcon.setSize("24");
        gridIcon.setFill(Color.valueOf("#a5a3a3"));
        gridIcon.setSize("24");
        initializeWebView();
        snackbar = new JFXSnackbar(rssViewContainer);
        initializePlusIcons();
        initializeNetworkTask();
        initGridViewListener();
        pocketShareMenu.disableProperty().bind(Bindings.not(configStorage.pocketEnabledProperty()));
    }

    private void initGridViewListener() {
        gridModeproperty.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && ! newValue) {
                splitPane.getItems().add(listViewContainer);
                splitPane.getItems().add(webViewContainer);
                splitPane.getItems().remove(gridContainer);
                switchViewButton.setGraphic(gridIcon);
                adjustSplitPaneDividers();
                
            }
            else if (newValue != null && newValue) {
                splitPane.getItems().remove(listViewContainer);
                splitPane.getItems().remove(webViewContainer);
                splitPane.getItems().add(gridContainer);
                switchViewButton.setGraphic(listIcon);
                adjustSplitPaneDividers();
            }
        });
        gridModeproperty.set(configStorage.prefersGridLayout());
    }

    private void eventHandleInitialize() {
        listView.setCellFactory(l -> new ItemListCell(this, connectServer));
        treeView.setCellFactory(t -> new MyTreeCell());
        gridView.setCellFactory(g -> new ItemGridCell(this, connectServer));
        
        //handle event between listView and webView
        listView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getSummary().getContent() != null) {
                if (rssRadioButton.isSelected()) {
                    webView.getEngine().loadContent(Item.processContent(newValue.getTitle(), newValue.getSummary().getContent()));
                } else if (webRadioButton.isSelected()) {
                    loadUrl(newValue.getCanonical().get(0).getHref());
                } else if (mercuryRadioButton.isSelected()) {
                    launchMercuryTask(newValue);
                }
                markItemRead(newValue);
                currentlySelectedItem = newValue;
            }
        }));
        
        //handle event between treeView and listView
        treeView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            
            LOGGER.debug("observableitemList size {}, observablereadItemList size {}", observableItemList.size(), observableReadList.size());
            if (starredList != null && newValue != null) {
                if (newValue.getValue().getId().equals("user/" + UserInfo.getUserId() + "/state/com.google/starred")) {
                    plusIcon.setVisible(false);
                    plusIconGrid.setVisible(false);
                    currentPredicate = p -> true;
                    filteredData = new FilteredList<>(FXCollections.observableArrayList(starredList), currentPredicate);
                    sortedData = new SortedList<>(filteredData, (o1, o2) -> (int) (Long.parseLong(o2.getCrawlTimeMsec()) - Long.parseLong(o1.getCrawlTimeMsec())));
                    listView.setItems(sortedData);
                    gridView.setItems(sortedData);
                    return;
                }
            }
            if (itemList != null && newValue != null) {
                if (newValue.getValue().getId().equals("All Items")) {//handle the special all items tag
                    plusIcon.setVisible(false);
                    plusIconGrid.setVisible(false);
                    currentPredicate = p -> true;
                    filteredData = new FilteredList<>(observableAllList, currentPredicate);
                    sortedData = new SortedList<>(filteredData, (o1, o2) -> (int) (Long.parseLong(o2.getCrawlTimeMsec()) - Long.parseLong(o1.getCrawlTimeMsec())));
                    listView.setItems(sortedData);
                    gridView.setItems(sortedData);
                    return;
                }
                if (newValue.isLeaf()) {//leaf chosen list
                    plusIcon.setVisible(true);
                    plusIconGrid.setVisible(true);
                    currentPredicate = (item) -> {
                        if (newValue.getValue().getId().equals(item.getOrigin().getStreamId())) {
                            return true;
                        }
                        return false;
                    };
                    filteredData = new FilteredList<>(observableAllList, currentPredicate);
                    sortedData = new SortedList<>(filteredData, (o1, o2) -> (int) (Long.parseLong(o2.getCrawlTimeMsec()) - Long.parseLong(o1.getCrawlTimeMsec())));
                    listView.setItems(sortedData);
                    gridView.setItems(sortedData);
                    return;
                    
                } else {//handle the folder chosen list
                    plusIcon.setVisible(false);
                    plusIconGrid.setVisible(false);
                    currentPredicate = (item) -> {
                        for (String s : item.getCategories()) {
                            if (newValue.getValue().getId().equals(s)) {
                                return true;
                            }
                        }
                        return false;
                    };
                    filteredData = new FilteredList<>(observableAllList, currentPredicate);
                    sortedData = new SortedList<>(filteredData, (o1, o2) -> (int) (Long.parseLong(o2.getCrawlTimeMsec()) - Long.parseLong(o1.getCrawlTimeMsec())));
                    listView.setItems(sortedData);
                    gridView.setItems(sortedData);
                    return;
                }
            }
            
            sortedData = new SortedList<>(filteredData, (o1, o2) -> (int) (Long.parseLong(o2.getCrawlTimeMsec()) - Long.parseLong(o1.getCrawlTimeMsec())));
            listView.setItems(sortedData);
            gridView.setItems(sortedData);
        }));
    }
    
    public void markItemRead(Item item) {
      //send mark feed read to server if not in the starred list
        if (!treeView.getSelectionModel().getSelectedItem().getValue().getId().equals("user/" + UserInfo.getUserId() + "/state/com.google/starred")) {
            if (!item.isRead()) {
                connectServer.markAsRead(item.getDecimalId());

                String streamId = item.getOrigin().getStreamId();
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
                Platform.runLater(() -> {
                    boolean removed = observableItemList.remove(item);
                    LOGGER.debug("remove = {}", removed);
                    boolean added = observableReadList.add(item);
                    LOGGER.debug("add = {}", added);
                    treeView.refresh();
                });
            }
            item.setRead(true);//change state to read and change color in listView
        }
    }

    private void initializeRadioButton() {
        //initialize the radio button
        ToggleGroup toggleGroup = new ToggleGroup();
        rssRadioButton.setToggleGroup(toggleGroup);
        webRadioButton.setToggleGroup(toggleGroup);
        mercuryRadioButton.setToggleGroup(toggleGroup);
        rssRadioButton.setSelected(true);
        toggleGroup.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {
            if (toggleGroup.getSelectedToggle() != null) {
                if (currentlySelectedItem != null) {
                    if (rssRadioButton.isSelected()) {
                        webView.getEngine().loadContent(Item.processContent(currentlySelectedItem.getTitle(), currentlySelectedItem.getSummary().getContent()));
                    } else if (webRadioButton.isSelected()) {
                        webView.getEngine().load(currentlySelectedItem.getCanonical().get(0).getHref());
                    } else if (mercuryRadioButton.isSelected()) {
                        launchMercuryTask(currentlySelectedItem);
                    }
                }
            }
        }));
    }
    
    private void launchMercuryTask(Item item) {
        String href = item.getCanonical().get(0).getHref();
        Task<MercuryResult> t = mercuryMobilizer.getMercuryResultTask(href);
        LOGGER.debug("{}",href);
        t.setOnSucceeded(e -> {
            webViewProgressBar.setVisible(false);
            MercuryResult m = t.getValue();
            LOGGER.debug("{}",m);
            if (m != null && ! m.getContent().isEmpty()) {
                String url = m.getUrl();
                if (url.startsWith("/")) {
                    url = href;
                }
                webView.getEngine().loadContent(MercuryMobilizer.formatContent(m.getImageUrl(), url, m.getContent()));
            }
        });
        connectServer.getTaskExecutor().submit(t);
        webViewProgressBar.setVisible(true);
    }
    
    private void initializeSearchBar() {
        FontAwesomeIconView searchIcon = new FontAwesomeIconView(FontAwesomeIcon.SEARCH, "15");
        searchBar.setLeft(searchIcon);
        rssSearchListener  = (obs, oldV, newV) -> {
            if (newV==null || newV.isEmpty()) {
//                LOGGER.debug("null empty");
                filteredData.setPredicate(null);
                filteredData.setPredicate(currentPredicate);
                return;
            }
//            LOGGER.debug("value {} - {}",oldV, newV);
            Predicate<Item> predicate  = item -> {
                // If filter text is empty, display all persons.
                if (filteredData.isEmpty()) {
                    return true;
                }
                if (item == null) {
                    return true;
                }
                String lowerCaseFilter = newV.toLowerCase();
                if (item.toString().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                }
                return false; // Does not match.
            };
                Predicate<Item> pred = predicate.and(currentPredicate);
                filteredData.setPredicate(pred);
                listView.refresh();
        };
        searchBar.textProperty().addListener(rssSearchListener);
    }
    
    private void initializeProgressBar() {
        webViewProgressBar.setVisible(false);
        webViewProgressBar.prefWidthProperty().bind(webViewContainer.widthProperty());
        progressBar.setVisible(false);
        progressBar.prefWidthProperty().bind(toolBarContainer.widthProperty());
        progressBar.progressProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.doubleValue() == 1.0) {
                    progressBar.setVisible(false);
                    treeView.setDisable(false);
                }
            }
        }));
    }
    
    private void initializePlusIcons() {
        plusIcon.setVisible(false);
        plusIcon.setOnMouseEntered(event -> plusIcon.setFill(Color.CORNFLOWERBLUE));
        plusIcon.setOnMouseExited(event -> plusIcon.setFill(Color.BLACK));
        plusIconGrid.setVisible(false);
        plusIconGrid.setOnMouseEntered(event -> plusIcon.setFill(Color.CORNFLOWERBLUE));
        plusIconGrid.setOnMouseExited(event -> plusIcon.setFill(Color.BLACK));
        
        Tooltip t = new Tooltip(bundle.getString("loadRead"));
        Tooltip.install(plusIcon, t);
        Tooltip.install(plusIconGrid, t);
        plusIcon.setOnMouseClicked(e -> loadOlderReadArticles());
        plusIconGrid.setOnMouseClicked(e -> loadOlderReadArticles());
    }
    
    private void initializeWebView() {
        webView.getEngine().setUserAgent("Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.113 Safari/537.36 JavaFx8");
        webView.setContextMenuEnabled(false);
        eventPrintingListener = event -> {
            LOGGER.debug("{}-{}",event.getURL(), event.getSource().getClass().getName());
            FXMain.getAppHostServices().showDocument(event.getURL().toString());
            return true;
        };
        webView.getEngine().getLoadWorker().stateProperty().addListener((ObservableValue<? extends State> p, State oldState, State newState) -> {
            if (newState == State.SUCCEEDED) {
                webView.setDisable(false);
                webViewProgressBar.setVisible(false);
                if (! isWebViewListenerAttached.get()) {
                    isWebViewListenerAttached.set(true);
                    WebViews.addHyperlinkListener(webView, eventPrintingListener, HyperlinkEvent.EventType.ACTIVATED);
                }
            }
            else if (newState == State.SCHEDULED || newState == State.RUNNING) {
                webView.setDisable(true);
                webViewProgressBar.setVisible(true);
            }
            else {
                webViewProgressBar.setVisible(false);
                webView.setDisable(false);
            }
        });
    }
    
    public void initializeNetworkTask() {
        Task<Void> t = connectServer.initTask();
        t.setOnSucceeded(e -> {
            progressBar.setVisible(false);
            refreshButton.setDisable(false);
            markReadButton.setDisable(false);
            addSubscriptionButton.setDisable(false);
            LOGGER.debug("Server successfully reached");
            snackbarNotify("Server successfully reached");
        });
        t.setOnRunning(e -> {
            progressBar.setVisible(true);
            LOGGER.debug("Trying to reach server...Please wait...");
            snackbarNotify("Trying to reach server...\nPlease wait...");
            refreshButton.setDisable(true);
            markReadButton.setDisable(true);
            addSubscriptionButton.setDisable(true);
        });
        t.setOnFailed(e -> {
            progressBar.setVisible(false);
            refreshButton.setDisable(false);
            addSubscriptionButton.setDisable(false);
            markReadButton.setDisable(false);
            LOGGER.debug("Failed to contact server");
            snackbarNotifyBlocking("Failed to contact server.\nCheck connection and retry in a moment");
        });
        this.connectServer.getTaskExecutor().submit(t);
    }

    private void initializeTasks() {
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
            progressBar.setProgress(progressBar.getProgress() + 0.25);
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
            Platform.runLater(() -> {
                observableItemList.clear();
                observableItemList.addAll(itemListTask.getValue());
                progressBar.setProgress(progressBar.getProgress() + 0.25);
            });
            LOGGER.debug("finish itemListTask " + observableItemList.size());
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
            progressBar.setProgress(progressBar.getProgress() + 0.25);
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
            progressBar.setProgress(progressBar.getProgress() + 0.25);
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
    public void switchView() {
        gridModeproperty.set(! gridModeproperty.get());
    }
    
    private void adjustSplitPaneDividers() {
        splitPane.setDividerPosition(0, 0.24);
        splitPane.setDividerPosition(1, 0.6);
    }

    @FXML
    public void refreshFired() {
        if (connectServer.isShouldAskPermissionOrLogin()) {
            snackbarNotifyBlocking(bundle.getString("pleaseLogin"));
        }
        else {
            progressBar.setVisible(true);
            progressBar.setProgress(0);
            initializeTasks();
            connectServer.getTaskExecutor().submit(unreadCountsTask);
            connectServer.getTaskExecutor().submit(treeTask);
            connectServer.getTaskExecutor().submit(itemListTask);
            connectServer.getTaskExecutor().submit(starredListTask);
            lastUpdateTime = Instant.now();
        }
    }

    @FXML
    private void markReadButtonFired() {
        if (connectServer.isShouldAskPermissionOrLogin()) {
            snackbarNotifyBlocking(bundle.getString("pleaseLogin"));
        } 
        else {
            List<Item> read = new ArrayList<>();
            for (Item item : listView.getItems()) {
                LOGGER.debug("observableitemList size {}, observablereadItemList size {}", observableItemList.size(), observableReadList.size());
                if (! item.isRead()) {
                    item.setRead(true);
                    read.add(item);
                }
            }
            Platform.runLater(() -> {
                boolean removed = observableItemList.removeAll(read);
                LOGGER.debug("remove = {}", removed);
                boolean added = observableReadList.addAll(read);
                LOGGER.debug("add = {}", added);
                LOGGER.debug("observableitemList size {}, observablereadItemList size {}", observableItemList.size(), observableReadList.size());
                listView.refresh();
                    
            });
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
                } 
                else if (!feed.getId().equals("All Items")) {//parent treeItems, except All Items
                    Integer count = unreadCountsMap.get(feed.getId());
                    unreadCountsMap.put(feed.getId(), 0);
                    for (TreeItem<Feed> son : treeView.getSelectionModel().getSelectedItem().getChildren()) {
                        unreadCountsMap.put(son.getValue().getId(), 0);
                    }
                    unreadCountsMap.put("All Items", unreadCountsMap.get("All Items") - count);
                } 
                else {//All Items
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
        }
    }
    
    private void loadUrl(String url) {
        webView.getEngine().load(url);
    }

    @FXML
    private void loginMenuFired() {
//        snackbarNotifyBlocking("toto");
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
    }
    
    public void showWebView(Item item) {
        if (popupWebViewStage == null) {
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(FXMain.getStage());
            dialogStage.getIcons().add(new Image("icon.png"));
            dialogStage.setResizable(true);
            Scene scene = new Scene(popupWebView.getView());
            dialogStage.setScene(scene);
            this.popupWebViewStage = dialogStage;
            popupWebViewController = (PopupWebViewController) popupWebView.getPresenter();
            popupWebViewController.setStage(dialogStage);
            popupWebViewController.setCurrentItem(item);
            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();
        }
        else {
            popupWebViewController.setCurrentItem(item);
            popupWebViewStage.showAndWait();
        }
    }
    
    private void loadOlderReadArticles() {
        Feed f = treeView.getSelectionModel().getSelectedItem().getValue();
        if (f == null) {
            return;
        }
        if (! (f instanceof Subscription)) {
            return;
        }
        int unreadAlreadyInMemory = unreadCountsMap.getOrDefault(f.getId(), 0);
        LOGGER.debug("older nb {}", unreadAlreadyInMemory);
        olderItemsListTask = connectServer.getOlderreadItemsTask(f.getId(), unreadAlreadyInMemory);
        olderItemsListTask.setOnRunning(e -> {
            progressBar.setVisible(true);
            progressBar.setProgress(-1);
            });
        olderItemsListTask.setOnFailed(e -> progressBar.setVisible(false));
        olderItemsListTask.setOnCancelled(e -> progressBar.setVisible(false));
        olderItemsListTask.setOnSucceeded(event -> {
            progressBar.setVisible(false);
            Platform.runLater(() -> {
            for (Item i : olderItemsListTask.getValue()) {
                if (observableReadList.stream().noneMatch(item -> item.getId().equals(i.getId()))) {
                        observableReadList.add(i);
                }
                else{
                    LOGGER.debug("not adding {}", i);
                }
            }
            LOGGER.debug("finish loadOlderReadArticles " + olderItemsListTask.getValue().size());
            int idx = treeView.getSelectionModel().getSelectedIndex();
            // hack to force content to be sorted after inserting older read items
            // in some cases the selection was not refreshed
            treeView.getSelectionModel().clearSelection();
            treeView.getSelectionModel().select(idx);
            });
        });     
        connectServer.getTaskExecutor().submit(olderItemsListTask);
    }
    
    @FXML
    public void shareItemPocket() {
        if (currentlySelectedItem != null) {
            if (! pocketClient.isConfigured()) {
                return;
            }
            if (FXMain.pocketAddLinkStage == null) {
                FXMain.createPocketAddLinkStage();
                Scene scene = new Scene(pocketAddLinkView.getView());
                FXMain.pocketAddLinkStage.setScene(scene);
                pocketAddLinkController = (PocketAddLinkController) pocketAddLinkView.getPresenter();
                pocketAddLinkController.setStage(FXMain.pocketAddLinkStage);
                pocketAddLinkController.setCurrentItem(currentlySelectedItem);
                // Show the dialog and wait until the user closes it
                FXMain.pocketAddLinkStage.showAndWait();
            }
            else {
                pocketAddLinkController.setCurrentItem(currentlySelectedItem);
                // Show the dialog and wait until the user closes it
                FXMain.pocketAddLinkStage.showAndWait();
            }
        }
    }
    
    @FXML
    public void displayAboutPopup() {
        if (aboutPopupStage == null) {
            Stage dialogStage = new Stage();
            dialogStage.setTitle(bundle.getString("about"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(FXMain.getStage());
            dialogStage.getIcons().add(new Image("icon.png"));
            dialogStage.setResizable(true);
            Scene scene = new Scene(aboutPopupView.getView());
            dialogStage.setScene(scene);
            this.aboutPopupStage = dialogStage;
            aboutPopupController = (AboutPopupController) aboutPopupView.getPresenter();
            aboutPopupController.setStage(dialogStage);
            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();
        }
        else {
            aboutPopupStage.showAndWait();
        }
        
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
    
    @FXML
    public void displaySettings() {
        FXMain.showView(SettingsView.class);
    }
    
    public void addToStarredList(Item item) {
        if (this.starredList != null) {
            if (this.starredList.stream().noneMatch(i -> i.getId().equals(item.getId()))) {
                this.starredList.add(item);
            }
        }
        int c = unreadCountsMap.getOrDefault("user/" + UserInfo.getUserId() + "/state/com.google/starred", 1);
        if (c > 1) {
            c++;
        }
        unreadCountsMap.put("user/" + UserInfo.getUserId() + "/state/com.google/starred", c);
        treeView.refresh();
    }
    
    public void removeFromStarredList(Item item) {
        if (this.starredList != null) {
            this.starredList.stream()
                            .filter(i -> i.getId().equals(item.getId()))
                            .findFirst()
                            .ifPresent(c -> this.starredList.remove(c));
        }
        int c = unreadCountsMap.getOrDefault("user/" + UserInfo.getUserId() + "/state/com.google/starred", 0);
        if (c > 0) {
            c--;
        }
        unreadCountsMap.put("user/" + UserInfo.getUserId() + "/state/com.google/starred", c);
        treeView.refresh();
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
    
    public static void snackbarNotifyBlocking(String msg) {
        snackbar.enqueue(new SnackbarEvent(msg, bundle.getString("snackbarClose") , 2500, true, e -> snackbar.close()));
    }
    
    public static void snackbarNotify(String msg) {
        if (msg != null) {
            snackbar.enqueue(new SnackbarEvent(msg, null, 1500, false, null));
        }
    }
}

