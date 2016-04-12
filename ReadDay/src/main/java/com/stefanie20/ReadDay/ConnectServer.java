package com.stefanie20.ReadDay;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

/**
 * This class provide functions to connect the server and get the related Reader.
 */
public class ConnectServer {
    public static String AppId = "1000001097";
    public static String AppKey = "ALuNA3QjaXb8fOiDC_n1pt0IR0wZMIIg";


    //URLs for InoReader
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
    public static String markStarredURL = "https://www.inoreader.com/reader/api/0/edit-tag?a=user/-/state/com.google/starred&i=";
    public static String markUnstarredURL ="https://www.inoreader.com/reader/api/0/edit-tag?r=user/-/state/com.google/starred&i=";
    public static String editSubscriptionURL = "https://www.inoreader.com/reader/api/0/subscription/edit?";
    public static String addSubscriptionURL = "https://www.inoreader.com/reader/api/0/subscription/quickadd?quickadd=";

    public static String code401 = "HTTP/1.1 401 Authorization Required";
    public static String code200 = "HTTP/1.1 200 OK";

    /**
     * Connect the server and get the Reader.
     * @param url the related URL in the field.
     * @return the BufferedReader which can be used by Gson to get information.
     */
    public static BufferedReader connectServer(String url) {
        try {
            URLConnection connection = new URL(url).openConnection();
            connection.setRequestProperty("AppId", AppId);
            connection.setRequestProperty("AppKey", AppKey);
            connection.setRequestProperty("Authorization", "GoogleLogin auth=" + UserInfo.getAuthString());

            connection.connect();

            //check http header
            Map<String, List<String>> header = connection.getHeaderFields();

            if (header.get(null).get(0).equals(code200)) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                return reader;
            } else {
                Platform.runLater(()->{
                    FXMain.getPrimaryStage().close();
                    Controller.getLoginStage().show();
                    Controller.getLoginController().setWarnLabelText(header.get(null).get(0));

                });
            }

        } catch (UnknownHostException uhe) {
            new Alert(Alert.AlertType.ERROR,uhe.getMessage()).show();
        } catch (MalformedURLException murle) {
            murle.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws Exception{
        BufferedReader reader = connectServer(ConnectServer.streamContentURL);
        PrintWriter pw = new PrintWriter("streamContent.txt");
        String s;
        while ((s = reader.readLine()) != null) {
            pw.println(s);
        }
        pw.close();
        reader.close();
    }

}