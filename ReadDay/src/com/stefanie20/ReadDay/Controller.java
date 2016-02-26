package com.stefanie20.ReadDay;

import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.WebView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by F317 on 16/2/22.
 */
public class Controller {
    @FXML
    private TreeView<Feed> treeView;
    @FXML
    private Button refreshButton;
    @FXML
    private ListView<Item> listView;
    @FXML
    private WebView webView;

    private List<Item> itemList;
    private List<Item> starredList;
    @FXML
    private void initialize() {
        eventHandleInitialize();
    }

    private void eventHandleInitialize() {
        listView.setCellFactory(l->new listCell());
        treeView.setCellFactory(t->new treeCell());


        //handle event between listView and webView
        listView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getSummary().getContent() != null) {
                webView.getEngine().loadContent(newValue.getSummary().getContent());
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
            listView.setItems(FXCollections.observableArrayList(chosenItemList));
        }));
    }

    static class listCell extends ListCell<Item> {
        @Override
        protected void updateItem(Item item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                setText(item.getOrigin().getTitle() + "    " + Instant.ofEpochSecond(item.getPublished()) + "\n" + item.getTitle());
            }
        }
    }

    static class treeCell extends TreeCell<Feed> {
        @Override
        protected void updateItem(Feed item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            }else{
                if (item instanceof Subscription) {
                    setText(((Subscription) item).getTitle());
                } else {
                    String s = item.getId();
                    setText(s.substring(s.lastIndexOf("/") + 1));
                }
            }
        }
    }



    @FXML
    private void refreshFired() {
            handleFolderFeedOrder();
            handleListView();
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
        try (BufferedReader reader = new BufferedReader(new FileReader("streamContent.txt"))) {
            Gson gson = new Gson();
            StreamContent content = gson.fromJson(reader, StreamContent.class);
            itemList = content.getItems();
        } catch (IOException e) {
        }







//        itemList = StreamContent.getStreamContent(ConnectServer.streamContentURL);
//        ObservableList<Item> observableList = FXCollections.observableArrayList(itemList);
//        listView.setItems(observableList);
        //get star list
//        starredList = StreamContent.getStreamContent(ConnectServer.starredContentURL);
//        ObservableList<Item> observableStarredList = FXCollections.observableArrayList(starredList);

    }
}

