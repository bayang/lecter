package com.stefanie20.ReadDay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

/**
 * Created by F317 on 16/2/22.
 */
public class ConnectServer {
    public static String AppId = "1000001097";
    public static String AppKey = "ALuNA3QjaXb8fOiDC_n1pt0IR0wZMIIg";

    public static String userinfoURL = "https://www.inoreader.com/reader/api/0/user-info";
    public static String loginURL = "https://www.inoreader.com/accounts/ClientLogin?";
    public static String unreadCountURL = "https://www.inoreader.com/reader/api/0/unread-count";
    public static String subscriptionListURL = "https://www.inoreader.com/reader/api/0/subscription/list";
    public static String folderTagListURL = "https://www.inoreader.com/reader/api/0/tag/list";
    public static String streamContentURL = "https://www.inoreader.com/reader/api/0/stream/contents/user/-/state/com.google/root?xt=user/-/state/com.google/read&n=100";
    public static String starredContentURL = "https://www.inoreader.com/reader/api/0/stream/contents/user/-/state/com.google/starred?n=100";
    public static String streamPreferenceListURL = "https://www.inoreader.com/reader/api/0/preference/stream/list";
    public static String itemIDsURL = "https://www.inoreader.com/reader/api/0/stream/items/ids?xt=user/-/state/com.google/read";
    public static String markAllReadURL = "https://www.inoreader.com/reader/api/0/mark-all-as-read?ts=";
    public static String markFeedReadURL = "https://www.inoreader.com/reader/api/0/edit-tag?a=user/-/state/com.google/read&i=";

    public static BufferedReader connectServer(String url) {
        try {
            URLConnection connection = new URL(url).openConnection();
            connection.setRequestProperty("AppId", AppId);
            connection.setRequestProperty("AppKey", AppKey);
            connection.setRequestProperty("Authorization", "GoogleLogin auth=" + UserInfo.getAuthString());

            connection.connect();

            //need to check the received header, do it in the future!!!
//            Map<String, List<String>> header = connection.getHeaderFields();
//            for (Map.Entry<String, List<String>> entry : header.entrySet()) {
//                System.out.println(entry);
//            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            return reader;
        } catch (MalformedURLException murle) {
            murle.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws Exception{
        BufferedReader reader = connectServer(unreadCountURL);
        System.out.println(reader.readLine());
    }

}
