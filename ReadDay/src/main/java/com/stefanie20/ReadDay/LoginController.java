package com.stefanie20.ReadDay;

import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

/**
 * The Controller used in the login panel.
 */
public class LoginController {

    @FXML
    private TextField emailTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Label warnLabel;




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


            //check http header
            Map<String, List<String>> header = connection.getHeaderFields();
            header.forEach((a, b) -> System.out.println(a + "   " + b));
            if (header.get(null).get(0).equals(ConnectServer.code200)) {
                Scanner scanner = new Scanner(connection.getInputStream());
                Properties properties = new Properties();

                scanner.nextLine();
                scanner.nextLine();
                String authString = scanner.nextLine().substring(5);
                properties.setProperty("Auth", authString);

                properties.store(new FileOutputStream(new File("UserInfo.dat")), null);
                UserInfo.setAuthString(authString);

                scanner.close();

                //close the login panel
                Controller.getLoginStage().close();
                System.out.println("login "+Controller.getLoginStage());
                FXMain.getPrimaryStage().show();
            } else if (header.get(null).get(0).equals(ConnectServer.code401)) {
                warnLabel.setText("Wrong ID or Password");
                return;
            } else {
                warnLabel.setText(header.get(null).get(0));
                return;
            }


        } catch (MalformedURLException murl) {
            murl.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        //get userInformation
        BufferedReader reader = ConnectServer.connectServer(ConnectServer.userinfoURL);
        Gson gson = new Gson();
        UserInformation userInformation = gson.fromJson(reader, UserInformation.class);
        Properties properties = new Properties();
        properties.setProperty("userId", userInformation.getUserId());
        UserInfo.setUserId(userInformation.getUserId());
        try {
            properties.store(new FileOutputStream("UserInfo.dat", true), null);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * A method to edit the warn label by other class.
     * @param s the text to show in the warn label.
     */
    public void setWarnLabelText(String s) {
        warnLabel.setText(s);
    }
}
