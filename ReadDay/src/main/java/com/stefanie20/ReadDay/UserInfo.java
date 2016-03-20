package com.stefanie20.ReadDay;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

/**
 * Created by F317 on 16/2/22.
 */
public class UserInfo {

    private static String userId;
    private static String authString;

    public static void setUserId(String userId) {
        UserInfo.userId = userId;
    }

    public static String getUserId() {
        return userId;
    }

    public static String getAuthString() {
        if (authString != null) {
            return authString;
        } else {
            File file = new File("UserInfo.dat");
            if (!file.exists()) {
                return null;
            } else {
                loadProperties(file);
            }
        }
        return authString;
    }

    public static void setAuthString(String authString) {
        UserInfo.authString = authString;
    }

    public static void loadProperties(File file) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(file));
            UserInfo.setAuthString(properties.getProperty("Auth"));
            UserInfo.setUserId(properties.getProperty("userId"));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
