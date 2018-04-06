package me.bayang.reader.view;

import com.jfoenix.controls.JFXSpinner;

import de.felixroske.jfxsupport.SplashScreen;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class SpinnerSplashScreen extends SplashScreen {

    @Override
    public Parent getParent() {
        JFXSpinner spinner = new JFXSpinner();
        Label appTitle  = new Label("lecter");
        appTitle.setFont(Font.font(30));
        appTitle.setStyle("-fx-text-fill : #90a4ae;");
        appTitle.setPadding(new Insets(0, 0, 10, 0));
        Label label  = new Label("Please wait...");
        label.setStyle("-fx-text-fill : white;");
        label.setFont(Font.font(20));
        final VBox vbox = new VBox();
        vbox.setPrefSize(400, 300);
        spinner.setPrefWidth(200);
        spinner.setPrefHeight(200);
        spinner.setMinWidth(200);
        spinner.setMinHeight(200);
        spinner.setRadius(200);
        spinner.setPadding(new Insets(0, 0, 10, 0));
        vbox.getChildren().addAll(appTitle, spinner, label);
        vbox.setStyle("-fx-background-color: #263238 ;");
        vbox.setAlignment(Pos.CENTER);

        return vbox;
    }

    @Override
    public boolean visible() {
        return super.visible();
    }

    @Override
    public String getImagePath() {
        return "";
    }
    
    

}
