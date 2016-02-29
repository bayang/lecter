package com.stefanie20.ReadDay;

import com.google.gson.Gson;
import com.stefanie20.ReadDay.ConnectServer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

/**
 * Created by F317 on 16/2/28.
 */
public class LoginController {

    @FXML
    private TextField emailTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;



    @FXML
    private void loginButtonFired() {
        String email = emailTextField.getText();
        String password = passwordField.getText();
        authenticate(email,password);

    }

    private void authenticate(String email, String password) {
        try {
            URL url = new URL("https://www.inoreader.com/accounts/ClientLogin?" + "Email=" + email + "&Passwd=" + password);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("AppId", ConnectServer.AppId);
            connection.setRequestProperty("AppKey", ConnectServer.AppKey);
            connection.connect();

            Scanner scanner = new Scanner(connection.getInputStream());
            PrintWriter printWriter = new PrintWriter("UserInfo.dat");

            scanner.nextLine();
            scanner.nextLine();
            String authString = scanner.nextLine();

            printWriter.println(authString);
            UserInfo.setAuthString(authString.substring(5));

            scanner.close();
            printWriter.close();
            //close the login panel
            Controller.getLoginStage().close();
            FXMain.getPrimaryStage().show();
        } catch (MalformedURLException murl) {
            murl.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        //get userInformation
        BufferedReader reader = ConnectServer.connectServer(ConnectServer.userinfoURL);
        Gson gson = new Gson();
        UserInformation userInformation = gson.fromJson(reader, UserInformation.class);
        try (PrintWriter printWriter = new PrintWriter(new FileOutputStream("UserInfo.dat", true))) {
            printWriter.println("userId=" + userInformation.getUserId());
            UserInfo.setUserId(userInformation.getUserId());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
