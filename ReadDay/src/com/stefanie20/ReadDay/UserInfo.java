package com.stefanie20.ReadDay;


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
        return authString;
    }

    public static void setAuthString(String authString) {
        UserInfo.authString = authString;
    }
}
