package com.stefanie20.ReadDay;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by F317 on 16/2/21.
 */
public class Test {

    private static String AppId = "1000001097";
    private static String AppKey = "ALuNA3QjaXb8fOiDC_n1pt0IR0wZMIIg";
    private static String Email = "zhang.stef@gmail.com";
    private static String Passwd = "tlSYCigsJ3koja";
    private static String Auth = "sWmAk7Od2RkZYj7FPJhaeyDG3hjpidZl";

    public static void main(String[] args) throws Exception{
        URL userinfoURL = new URL("https://www.inoreader.com/reader/api/0/user-info");
        URL loginURL = new URL("https://www.inoreader.com/accounts/ClientLogin?" + "Email=" + Email + "&Passwd=" + Passwd);
        URL unreadCountURL = new URL("https://www.inoreader.com/reader/api/0/unread-count");
        URL subscriptionListURL = new URL("https://www.inoreader.com/reader/api/0/subscription/list");
        URL folderTagListURL = new URL("https://www.inoreader.com/reader/api/0/tag/list");
        URL streamContentURL = new URL("https://www.inoreader.com/reader/api/0/stream/contents/user/-/state/com.google/root?xt=user/-/state/com.google/read&n=100");
        URL streamPreferenceListURL = new URL("https://www.inoreader.com/reader/api/0/preference/stream/list");
        URL itemIDsURL = new URL("https://www.inoreader.com/reader/api/0/stream/items/ids?xt=user/-/state/com.google/read");
        URL markReadURL = new URL("https://www.inoreader.com/reader/api/0/mark-all-as-read?s=tag:google.com,2005:reader/item/00000001c3c49089");
        URL editURL = new URL("https://www.inoreader.com/reader/api/0/edit-tag?a=user/-/state/com.google/read&i=");
        getInfo(editURL);
//        URLConnection connection = streamContentURL.openConnection();
//        connection.setRequestProperty("AppId", AppId);
//        connection.setRequestProperty("AppKey", AppKey);
//        connection.setRequestProperty("Authorization", "GoogleLogin auth=" + Auth);
//        connection.connect();
//        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//        Gson gson = new Gson();
//        SubscriptionsList list = gson.fromJson(subscriptionListString, SubscriptionsList.class);
//        System.out.println(list.getSubscriptions().get(0).getCategories().get(0).getId());

    }

    public static void getInfo(URL url) {
        try {
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("AppId", AppId);
            connection.setRequestProperty("AppKey", AppKey);
            connection.setRequestProperty("Authorization", "GoogleLogin auth=" + Auth);
            Map<String, List<String>> header = connection.getHeaderFields();
            for (Map.Entry<String, List<String>> entry : header.entrySet()) {
                System.out.println(entry);
            }
            System.out.println("******************************");
            Scanner sc = new Scanner(connection.getInputStream());
            PrintWriter pw = new PrintWriter("output.txt");
            while (sc.hasNextLine()) {
                System.out.println(sc.nextLine());
//                pw.printf(sc.nextLine());
            }
            pw.close();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }
}
