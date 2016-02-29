package com.stefanie20.ReadDay;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * Created by F317 on 16/2/22.
 */
public class FXMain extends Application{
    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("UI.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("ReadDay");
            stage.show();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(11);
        }


    }

}
