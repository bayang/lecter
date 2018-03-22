package me.bayang.reader.rssmodels;

public class UserInfo {

    private static String userId;
    private static String authString;

    public static void setUserId(String userId) {
        UserInfo.userId = userId;
    }

    public static String getUserId() {
        return userId;
    }

}
