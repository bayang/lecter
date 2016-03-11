package com.stefanie20.ReadDay;


import java.io.File;
import java.io.FileNotFoundException;
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
                try (Scanner scanner = new Scanner(file)) {
                    UserInfo.setAuthString(scanner.nextLine().substring(5));
                    UserInfo.setUserId(scanner.nextLine().substring(7));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return authString;
    }

    public static void setAuthString(String authString) {
        UserInfo.authString = authString;
    }
}
