package com.stefanie20.ReadDay;

import com.stefanie20.ReadDay.Controllers.AddSubscriptionController;
import com.stefanie20.ReadDay.Controllers.LoginController;
import com.stefanie20.ReadDay.RSSModels.UserInfo;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main function for JavaFX
 */
public class FXMain extends Application {
    private static Stage primaryStage, loginStage, addSubscriptionStage;
    private static FXMLLoader loader = new FXMLLoader();
    private static LoginController loginController;
    private static AddSubscriptionController addSubscriptionController;

    @Override

    public void start(Stage stage) {
        primaryStage = stage;
        initPrimaryStage();
        initLoginStage();
        initAddSubscriptionStage();
        showFirstStage();
    }

    private void initPrimaryStage() {
        Parent root = null;
        try {
            root = loader.load(getClass().getClassLoader().getResource("UI.fxml").openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("ReadDay");
        primaryStage.getIcons().add(new Image("icon.png"));
    }

    private void initLoginStage() {
        loginStage = new Stage();
        loginStage.setTitle("Login");
        loginStage.getIcons().add(new Image("icon.png"));
        loginStage.setResizable(false);
        FXMLLoader loginLoader = new FXMLLoader();
        try {
            Parent loginNode = loginLoader.load(getClass().getClassLoader().getResource("LoginPanel.fxml").openStream());
            loginController = loginLoader.getController();
            loginStage.setScene(new Scene(loginNode));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initAddSubscriptionStage() {
        addSubscriptionStage = new Stage();
        addSubscriptionStage.setTitle("Add Subscription");
        addSubscriptionStage.getIcons().add(new Image("icon.png"));
        addSubscriptionStage.setResizable(false);
        FXMLLoader loader = new FXMLLoader();

        try {
            loader.setLocation(getClass().getClassLoader().getResource("AddSubscriptionPanel.fxml"));
            Parent node = loader.load(getClass().getClassLoader().getResource("AddSubscriptionPanel.fxml").openStream());
            addSubscriptionController = loader.getController();
            addSubscriptionStage.setScene(new Scene(node));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void showFirstStage() {
        if (UserInfo.getAuthString() == null) {
            loginStage.show();
        } else {
            primaryStage.show();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * @return the primaryStage
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setPrimaryStage(Stage primaryStage) {
        FXMain.primaryStage = primaryStage;
    }

    public static LoginController getLoginController() {
        return loginController;
    }
    public static Stage getLoginStage() {
        return loginStage;
    }

    public static Stage getAddSubscriptionStage() {
        return addSubscriptionStage;
    }
}
