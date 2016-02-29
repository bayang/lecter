package com.stefanie20.ReadDay;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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


    private List<Item> itemList;
    private List<Item> starredList;
    private Instant lastUpdateTime;
    private Stage loginStage;

    @FXML
    private void initialize() {
        eventHandleInitialize();
        loginPaneInitialize();
        userInfoInitialize();
    }

    private void userInfoInitialize() {
        File file = new File("UserInfo.dat");
        if (!file.exists()) {
            loginStage.show();
        } else {
            try (Scanner scanner = new Scanner(file)) {
                UserInfo.setAuthString(scanner.nextLine().substring(5));
                UserInfo.setUserId(scanner.nextLine().substring(7));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    private void loginPaneInitialize() {
        loginStage = new Stage();
        loginStage.setTitle("Login");
        loginStage.setScene(new Scene(loginPane));
        loginStage.setResizable(false);
    }
    @FXML
    private void loginMenuFired() {
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
                webView.getEngine().loadContent(newValue.getSummary().getContent());
                newValue.setRead(true);//change state to read and change color in listView
                listView.refresh();//force refresh, or it will take a while to show the difference
                //send mark feed read to server
                new Thread(()->ConnectServer.connectServer(ConnectServer.markFeedReadURL+newValue.getDecimalId())).start();
            }
        }));
        //handle event between treeView and listView
        treeView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            List<Item> chosenItemList = new ArrayList<>();

            if (starredList != null) {
                if (newValue.getValue().getId().equals("user/"+UserInfo.getUserId()+"/state/com.google/starred")) {
                    chosenItemList = starredList;
                }
            }
            if (itemList != null) {
                if (newValue.getValue().getId().equals("All Items")) {//handle the special all items tag
                    chosenItemList = itemList;
                }
                if (newValue.isLeaf()) {//leaf chosen list
                    for (Item item : itemList) {
                        if (newValue.getValue().getId().equals(item.getOrigin().getStreamId())) {
                            chosenItemList.add(item);
                        }
                    }
                } else{//handle the folder chosen list
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
                if (item instanceof Subscription) {
                    String title = ((Subscription) item).getTitle();
                    setText(title);
                    if (FolderFeedOrder.iconMap != null) {
                        ImageView imageView = new ImageView(FolderFeedOrder.iconMap.get(item.getId()));
                        imageView.setFitHeight(16);
                        imageView.setFitWidth(16);
                        setGraphic(imageView);
                    }
                } else {
                    String s = item.getId();
                    setText(s.substring(s.lastIndexOf("/") + 1));
                }
            }
        }
    }



    @FXML
    private void refreshFired() {
        if (UserInfo.getAuthString() == null) {
            statusLabel.setText("Please Login");
        } else {
            statusLabel.setText("Refreshing...");
            handleFolderFeedOrder();
            handleListView();
            lastUpdateTime = Instant.now();
            statusLabel.setText("Refresh Complete");
        }
    }
    @FXML
    private void markReadButtonFired() {
        if (UserInfo.getAuthString() == null) {
            statusLabel.setText("Please Login");
        } else {
            for (Item item : listView.getItems()) {
                item.setRead(true);
            }
            listView.refresh();

            new Thread(() -> {
                ConnectServer.connectServer(ConnectServer.markAllReadURL + lastUpdateTime.getEpochSecond() + "&s=" + treeView.getSelectionModel().getSelectedItem().getValue().getId());
            }).start();
        }
    }

    private void handleFolderFeedOrder() {
        TreeItem<Feed> root = new TreeItem<>(new Tag("root","root")); //the root node doesn't show;
        root.setExpanded(true);
        treeView.setRoot(root);
        treeView.setShowRoot(false);

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

//        System.out.println(map);
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
}

