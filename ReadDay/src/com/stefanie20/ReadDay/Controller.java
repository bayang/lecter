package com.stefanie20.ReadDay;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

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
    private void initialize() {

    }

    @FXML
    private void refreshFired() {
        handleFolderFeedOrder();
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
}

