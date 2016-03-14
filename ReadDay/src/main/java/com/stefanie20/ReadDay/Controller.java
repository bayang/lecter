package com.stefanie20.ReadDay;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Created by F317 on 16/2/22.
 */
public class Controller {
    @FXML
    private AnchorPane loginPane;
    @FXML
    private TreeView<Feed> treeView;
    @FXML
    private Button refreshButton;
    @FXML
    private ListView<Item> listView;
    @FXML
    private WebView webView;
    @FXML
    private Button markReadButton;
    @FXML
    private Label statusLabel;
    @FXML
    private RadioButton rssRadioButton;
    @FXML
    private RadioButton webRadioButton;


    private List<Item> itemList;
    private List<Item> starredList;
    private Instant lastUpdateTime;
    private static Stage loginStage;
    private Task<TreeItem<Feed>> treeTask;
    private Task<List<Item>> itemListTask;
    private Task<List<Item>> starredListTask;
    private Task<Map<String,Integer>> unreadCountsTask;
    private static Map<String,Integer> unreadCountsMap;
    private static TreeItem<Feed> root;

    @FXML
    private void initialize() {
//        taskInitialize();
        eventHandleInitialize();
//        loginPaneInitialize();
        userInfoInitialize();
        radioButtonInitialize();
    }

    private void userInfoInitialize() {
        File file = new File("UserInfo.dat");
        if (!file.exists()) {
            if (loginStage == null) {
                loginPaneInitialize();
            }
            loginStage.show();
        } else {
            FXMain.getPrimaryStage().show();
            try (Scanner scanner = new Scanner(file)) {
                UserInfo.setAuthString(scanner.nextLine().substring(5));
                UserInfo.setUserId(scanner.nextLine().substring(7));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    private void loginPaneInitialize() {//lazy init
        loginStage = new Stage();
        loginStage.setTitle("Login");
        loginStage.setScene(new Scene(loginPane));
        loginStage.setResizable(false);
    }
    @FXML
    private void loginMenuFired() {
        if (loginStage == null) {
            loginPaneInitialize();
        }
            loginStage.show();
    }
    @FXML
    private void exitMenuFired() {
        System.exit(0);
    }


    private void eventHandleInitialize() {
        listView.setCellFactory(l->new listCell());
        treeView.setCellFactory(t->new treeCell());


        //handle event between listView and webView
        listView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getSummary().getContent() != null) {
                if (rssRadioButton.isSelected()) {
                    webView.getEngine().loadContent(newValue.getSummary().getContent());
                } else if (webRadioButton.isSelected()) {
                    webView.getEngine().load(newValue.getCanonical().get(0).getHref());
                }
                //send mark feed read to server if not in the starred list
                if (!treeView.getSelectionModel().getSelectedItem().getValue().getId().equals("user/" + UserInfo.getUserId() + "/state/com.google/starred")) {
                    if (!newValue.isRead()) {
                        new Thread(() -> ConnectServer.connectServer(ConnectServer.markFeedReadURL + newValue.getDecimalId())).start();
                        String streamId = newValue.getOrigin().getStreamId();
                        Integer count = unreadCountsMap.get(streamId);
                        unreadCountsMap.put(streamId, --count);
                        Integer allCount = unreadCountsMap.get("All Items");
                        unreadCountsMap.put("All Items", --allCount);
                        //set parent count
                        String parent = getParentItem(streamId).getValue().getId();
                        Integer parentCount = unreadCountsMap.get(parent);
                        unreadCountsMap.put(parent, --parentCount);
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

            if (starredList != null && newValue != null) {
                if (newValue.getValue().getId().equals("user/" + UserInfo.getUserId() + "/state/com.google/starred")) {
                    chosenItemList = starredList;
                }
            }
            if (itemList != null && newValue != null) {
                if (newValue.getValue().getId().equals("All Items")) {//handle the special all items tag
                    chosenItemList = itemList;
                }
                if (newValue.isLeaf()) {//leaf chosen list
                    for (Item item : itemList) {
                        if (newValue.getValue().getId().equals(item.getOrigin().getStreamId())) {
                            chosenItemList.add(item);
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
                }
            }
            chosenItemList.sort((o1, o2) -> (int) (Long.parseLong(o1.getCrawlTimeMsec()) - Long.parseLong(o2.getCrawlTimeMsec())));
            listView.setItems(FXCollections.observableArrayList(chosenItemList));
        }));


    }

    private void taskInitialize() {
        //when get the treeItem from the URL, change the view
        treeTask = new Task<TreeItem<Feed>>() {
            @Override
            protected TreeItem<Feed> call() throws Exception {
                System.out.println("start treeTask");
                return handleFolderFeedOrder();
            }
        };
        treeTask.setOnSucceeded(event -> {
            treeView.setRoot(treeTask.getValue());
            treeView.setShowRoot(false);
            statusLabel.setText("Get Feeds Complete.");
            System.out.println("finish treeTask");
        });

        //initialize itemList
        itemListTask = new Task<List<Item>>() {
            @Override
            protected List<Item> call() throws Exception {
                System.out.println("start itemListTask");
                return StreamContent.getStreamContent(ConnectServer.streamContentURL);
            }
        };
        itemListTask.setOnSucceeded(event -> {
            itemList = itemListTask.getValue();
            statusLabel.setText("Get New Items Complete.");
            System.out.println("finish itemListTask");
        });
        //initialize starredList
        starredListTask = new Task<List<Item>>() {
            @Override
            protected List<Item> call() throws Exception {
                System.out.println("start starredListTask");
                return StreamContent.getStreamContent(ConnectServer.starredContentURL);
            }
        };
        starredListTask.setOnSucceeded(event -> {
            starredList = starredListTask.getValue();
            statusLabel.setText("Get Starred Items Complete.");
            System.out.println("finish starredListTask");
        });
        //initialize unreadCountsMap
        unreadCountsTask = new Task<Map<String, Integer>>() {
            @Override
            protected Map<String, Integer> call() throws Exception {
                System.out.println("start unreadCountsTask");
                return UnreadCounter.getUnreadCountsMap();
            }
        };
        unreadCountsTask.setOnSucceeded(event -> {
            unreadCountsMap = unreadCountsTask.getValue();
            statusLabel.setText("Get UnreadCounts Complete");
            System.out.println("finish unreadCountsTask");
        });
    }

    private void radioButtonInitialize() {
        //set webView User Agent
        webView.getEngine().setUserAgent("Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36");

        //initialize the radio button
        ToggleGroup toggleGroup = new ToggleGroup();
        rssRadioButton.setToggleGroup(toggleGroup);
        webRadioButton.setToggleGroup(toggleGroup);
        rssRadioButton.setSelected(true);
        toggleGroup.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {
            if (toggleGroup.getSelectedToggle() != null) {
                if (listView.getSelectionModel().getSelectedItem() != null) {
                    if (rssRadioButton.isSelected()) {
                        webView.getEngine().loadContent(listView.getSelectionModel().getSelectedItem().getSummary().getContent());
                    } else if (webRadioButton.isSelected()) {
                        webView.getEngine().load(listView.getSelectionModel().getSelectedItem().getCanonical().get(0).getHref());
                    }
                }
            }
        }));

    }

    static class listCell extends ListCell<Item> {//set the listView show content and icon
        private static ZoneId defaultZoneId = ZoneId.systemDefault();
        @Override
        protected void updateItem(Item item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                //get the time style
                LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(item.getCrawlTimeMsec())), defaultZoneId);
                String timeString = localDateTime.toLocalTime().toString();
                if (!localDateTime.toLocalDate().equals(LocalDate.now())) {
                    timeString = localDateTime.toString();
                }
                setText(item.getOrigin().getTitle() + "    " + timeString + "\n" + item.getTitle());
                setTextFill(item.isRead()? Color.GRAY:Color.BLACK);
                if (FolderFeedOrder.iconMap != null) {
                    ImageView imageView = new ImageView(FolderFeedOrder.iconMap.get(item.getOrigin().getStreamId()));
                    imageView.setFitWidth(16);
                    imageView.setFitHeight(16);
                    setGraphic(imageView);
                }
            }
        }
    }

    static class treeCell extends TreeCell<Feed> {
        @Override
        protected void updateItem(Feed item, boolean empty) {//set the treeView style show title and icons
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            }else{
                HBox hBox = new HBox();
                hBox.setSpacing(10);
                hBox.setMaxWidth(250);
                if (item instanceof Subscription) {
                    String title = ((Subscription) item).getTitle();
                    Integer countInteger = unreadCountsMap.get(item.getId());
                    //create spaces to make counts in a row
                    Label countLabel = new Label(Objects.toString(countInteger, ""));
                    Label titleLabel = new Label(title);
                    titleLabel.setPrefWidth(150);
                    //using HBox to add all the content
                    if (FolderFeedOrder.iconMap != null) {
                        ImageView imageView = new ImageView(FolderFeedOrder.iconMap.get(item.getId()));
                        imageView.setFitHeight(16);
                        imageView.setFitWidth(16);
                        hBox.getChildren().addAll(imageView, titleLabel, countLabel);
                    } else {
                        hBox.getChildren().addAll(titleLabel, countLabel);
                    }

                } else {
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
    private void refreshFired() {
        if (UserInfo.getAuthString() == null) {
            statusLabel.setText("Please Login");
        } else {
            statusLabel.setText("Refreshing...");
            taskInitialize();
            new Thread(treeTask).start();
            new Thread(itemListTask).start();
            new Thread(unreadCountsTask).start();
            new Thread(starredListTask).start();
//            handleFolderFeedOrder();
//            handleListView();
            lastUpdateTime = Instant.now();
        }
    }
    @FXML
    private void markReadButtonFired() {
        if (UserInfo.getAuthString() == null) {
            new Alert(Alert.AlertType.ERROR, "Please Login.");
        } else {
            for (Item item : listView.getItems()) {
                item.setRead(true);
            }
            listView.refresh();
            //inform treeView to refresh the unread count
            if (!treeView.getSelectionModel().getSelectedItem().getValue().getId().equals("user/" + UserInfo.getUserId() + "/state/com.google/starred")) {
                Feed feed = treeView.getSelectionModel().getSelectedItem().getValue();
                if (feed instanceof Subscription) {
                    Integer count = unreadCountsMap.get(feed.getId());
                    unreadCountsMap.put(feed.getId(), 0);
                    Feed parent = treeView.getSelectionModel().getSelectedItem().getParent().getValue();
                    unreadCountsMap.put(parent.getId(), unreadCountsMap.get(parent.getId()) - count);
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
                    for (TreeItem<Feed> parent : root.getChildren()) {
                        if (!parent.getValue().getId().equals("user/" + UserInfo.getUserId() + "/state/com.google/starred")) {
                            if (parent.getValue() instanceof Tag) {
                                for (TreeItem<Feed> son : parent.getChildren()) {
                                    unreadCountsMap.put(son.getValue().getId(), 0);
                                }
                            }
                            unreadCountsMap.put(parent.getValue().getId(), 0);
                        }
                    }
                }
            }
            treeView.refresh();




            new Thread(() -> {
                ConnectServer.connectServer(ConnectServer.markAllReadURL + lastUpdateTime.getEpochSecond() + "&s=" + treeView.getSelectionModel().getSelectedItem().getValue().getId());
            }).start();
        }
    }

    private TreeItem<Feed> handleFolderFeedOrder() {
        root = new TreeItem<>(new Tag("root","root")); //the root node doesn't show;
        root.setExpanded(true);

        Map<Feed, List<Subscription>> map = FolderFeedOrder.getOrder();
        for (Feed feed : map.keySet()) {
//            root.getChildren().add(new TreeItem(feed));
            TreeItem<Feed> tag = new TreeItem<>(feed);
            if (map.get(feed)!=null) {
                for (Subscription sub : map.get(feed)) {
                    TreeItem<Feed> subscription = new TreeItem<>(sub);
                    tag.getChildren().add(subscription);
                }
            }
            root.getChildren().add(tag);
        }
        return root;

//        treeView.setRoot(root);
//        treeView.setShowRoot(false);
    }

    private void handleListView() {
//        try (BufferedReader reader = new BufferedReader(new FileReader("streamContent.txt"))) {
//            Gson gson = new Gson();
//            StreamContent content = gson.fromJson(reader, StreamContent.class);
//            itemList = content.getItems();
//        } catch (IOException e) {
//        }







        itemList = StreamContent.getStreamContent(ConnectServer.streamContentURL);
//        ObservableList<Item> observableList = FXCollections.observableArrayList(itemList);
//        listView.setItems(observableList);
        //get star list
        starredList = StreamContent.getStreamContent(ConnectServer.starredContentURL);
//        ObservableList<Item> observableStarredList = FXCollections.observableArrayList(starredList);

    }

    public static Stage getLoginStage() {
        return loginStage;
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
}

