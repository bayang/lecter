package com.stefanie20.ReadDay;

import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * Created by F317 on 16/2/22.
 */
public class Controller {
    @FXML
    private TreeView<String> treeView;

    @FXML
    private void initialize() {
        TreeItem<String> root = new TreeItem<>("Root Node");
        root.setExpanded(true);
        root.getChildren().addAll(
                new TreeItem<String>("Item 1"),
                new TreeItem<String>("Item 2")
        );
        treeView.setRoot(root);
    }
}
