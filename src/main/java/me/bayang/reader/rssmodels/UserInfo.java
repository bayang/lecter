package me.bayang.reader.rssmodels;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.dmfs.httpessentials.exceptions.ProtocolException;
import org.dmfs.oauth2.client.OAuth2AccessToken;

/**
 * This class is used to store userId and authString, which is used in connecting server and sort data.
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

//    public static String getAuthString() {
//        if (authString != null) {
//            return authString;
//        } else {
//            File file = new File("UserInfo.dat");
//            if (!file.exists()) {
//                return null;
//            } else {
//                loadProperties(file);
//            }
//        }
//        return authString;
//    }
    
//    public static void saveToken(OAuth2AccessToken token) throws FileNotFoundException, IOException, ProtocolException {
//        Properties properties = new Properties();
//        String accessToken = String.valueOf(token.accessToken());
//        String refreshToken = String.valueOf(token.refreshToken());
//        String scope = token.scope().toString();
//        try {
//            String accessTokenExpiration = token.expirationDate().toString();
//            properties.setProperty("expiration", accessTokenExpiration);
//        } catch (ProtocolException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        properties.setProperty("refresh", refreshToken);
//        properties.setProperty("access", accessToken);
//        properties.setProperty("scope", scope);
//        properties.store(new FileOutputStream(new File("UserInfo.dat")), null);
//    }
//
//    public static void setAuthString(String authString) {
//        UserInfo.authString = authString;
//    }
//
//    public static void loadProperties(File file) {
//        Properties properties = new Properties();
//        try {
//            properties.load(new FileInputStream(file));
//            UserInfo.setAuthString(properties.getProperty("Auth"));
//            UserInfo.setUserId(properties.getProperty("userId"));
//        } catch (IOException ioe) {
//            ioe.printStackTrace();
//        }
//    }
}
