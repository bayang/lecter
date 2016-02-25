package com.stefanie20.ReadDay;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.web.WebView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    @FXML
    private void initialize() {
        eventHandleInitialize();
    }

    private void eventHandleInitialize() {
        //handle event between listView and webView
        listView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getSummary().getContent() != null) {
                webView.getEngine().loadContent(newValue.getSummary().getContent());
            }
        }));
        //handle event between treeView and listView
        treeView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            List<Item> chosenItemList = new ArrayList<>();

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

    @FXML
    private void refreshFired() {
        Platform.runLater(()->{
            handleFolderFeedOrder();
            handleListView();
        });

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
        itemList = StreamContent.getStreamContent();
        ObservableList<Item> observableList = FXCollections.observableArrayList(itemList);
        listView.setItems(observableList);
    }
}

