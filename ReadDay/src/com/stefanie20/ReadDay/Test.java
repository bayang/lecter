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
//        String subscriptionListString = "{\"subscriptions\":[{\"id\":\"feed\\/http:\\/\\/feed.appinn.com\\/\",\"title\":\"小众软件 - Appinn\",\"categories\":[{\"id\":\"user\\/1005907486\\/label\\/软件\",\"label\":\"软件\"}],\"sortid\":\"01370A2D\",\"firstitemmsec\":1448529368218418,\"url\":\"http:\\/\\/feed.appinn.com\\/\",\"htmlUrl\":\"http:\\/\\/www.appinn.com\\/\",\"iconUrl\":\"https:\\/\\/www.inoreader.com\\/cache\\/favicons\\/a\\/p\\/p\\/www_appinn_com_48x48.png\"},{\"id\":\"feed\\/http:\\/\\/feed.google.org.cn\\/\",\"title\":\"谷奥——探寻谷歌的奥秘\",\"categories\":[{\"id\":\"user\\/1005907486\\/label\\/IT\",\"label\":\"IT\"}],\"sortid\":\"01370A39\",\"firstitemmsec\":1455847200000000,\"url\":\"http:\\/\\/feed.google.org.cn\\/\",\"htmlUrl\":\"http:\\/\\/each.fm\\/\",\"iconUrl\":\"https:\\/\\/www.inoreader.com\\/cache\\/favicons\\/e\\/a\\/c\\/each_fm_32x32.png\"},{\"id\":\"feed\\/http:\\/\\/songshuhui.net\\/feed\",\"title\":\"科学松鼠会\",\"categories\":[{\"id\":\"user\\/1005907486\\/label\\/生活\",\"label\":\"生活\"}],\"sortid\":\"01370A43\",\"firstitemmsec\":1448529368218418,\"url\":\"http:\\/\\/songshuhui.net\\/feed\",\"htmlUrl\":\"http:\\/\\/songshuhui.net\\/\",\"iconUrl\":\"https:\\/\\/www.inoreader.com\\/cache\\/favicons\\/s\\/o\\/n\\/songshuhui_net_32x32.png\"},{\"id\":\"feed\\/http:\\/\\/feed.williamlong.info\\/\",\"title\":\"月光博客\",\"categories\":[{\"id\":\"user\\/1005907486\\/label\\/IT\",\"label\":\"IT\"}],\"sortid\":\"01370A3E\",\"firstitemmsec\":1455847200000000,\"url\":\"http:\\/\\/feed.williamlong.info\\/\",\"htmlUrl\":\"http:\\/\\/www.williamlong.info\\/\",\"iconUrl\":\"https:\\/\\/www.inoreader.com\\/cache\\/favicons\\/w\\/i\\/l\\/www_williamlong_info_16x16.png\"},{\"id\":\"feed\\/http:\\/\\/feeds.reuters.com\\/reuters\\/topNews\",\"title\":\"Reuters: Top News\",\"categories\":[{\"id\":\"user\\/1005907486\\/label\\/News\",\"label\":\"News\"}],\"sortid\":\"01AB0FA5\",\"firstitemmsec\":1455882211088742,\"url\":\"http:\\/\\/feeds.reuters.com\\/reuters\\/topNews\",\"htmlUrl\":\"http:\\/\\/www.reuters.com\\/\",\"iconUrl\":\"https:\\/\\/www.inoreader.com\\/cache\\/favicons\\/r\\/e\\/u\\/www_reuters_com_16x16.png\"},{\"id\":\"feed\\/http:\\/\\/www.zhihu.com\\/rss\",\"title\":\"知乎精选\",\"categories\":[{\"id\":\"user\\/1005907486\\/label\\/生活\",\"label\":\"生活\"}],\"sortid\":\"01370A42\",\"firstitemmsec\":1448529368218418,\"url\":\"http:\\/\\/www.zhihu.com\\/rss\",\"htmlUrl\":\"http:\\/\\/www.zhihu.com\\/\",\"iconUrl\":\"https:\\/\\/www.inoreader.com\\/cache\\/favicons\\/z\\/h\\/i\\/www_zhihu_com_16x16.png\"},{\"id\":\"feed\\/http:\\/\\/feeds.maketecheasier.com\\/MakeTechEasier\",\"title\":\"Make Tech Easier\",\"categories\":[{\"id\":\"user\\/1005907486\\/label\\/IT\",\"label\":\"IT\"}],\"sortid\":\"01370A3F\",\"firstitemmsec\":1455847200000000,\"url\":\"http:\\/\\/feeds.maketecheasier.com\\/MakeTechEasier\",\"htmlUrl\":\"http:\\/\\/www.maketecheasier.com\\/\",\"iconUrl\":\"https:\\/\\/www.inoreader.com\\/cache\\/favicons\\/m\\/a\\/k\\/www_maketecheasier_com_16x16.png\"},{\"id\":\"feed\\/http:\\/\\/9to5mac.com\\/feed\\/\",\"title\":\"9to5Mac\",\"categories\":[{\"id\":\"user\\/1005907486\\/label\\/Apple\",\"label\":\"Apple\"}],\"sortid\":\"01996274\",\"firstitemmsec\":1448529368218418,\"url\":\"http:\\/\\/9to5mac.com\\/feed\\/\",\"htmlUrl\":\"http:\\/\\/9to5mac.com\\/\",\"iconUrl\":\"https:\\/\\/www.inoreader.com\\/cache\\/favicons\\/9\\/t\\/o\\/9to5mac_com_16x16.png\"},{\"id\":\"feed\\/http:\\/\\/feed.read.org.cn\\/\",\"title\":\"战隼的学习探索\",\"categories\":[{\"id\":\"user\\/1005907486\\/label\\/学习\",\"label\":\"学习\"}],\"sortid\":\"01370A4F\",\"firstitemmsec\":1448529368218418,\"url\":\"http:\\/\\/feed.read.org.cn\\/\",\"htmlUrl\":\"http:\\/\\/www.read.org.cn\\/\",\"iconUrl\":\"https:\\/\\/www.inoreader.com\\/cache\\/favicons\\/r\\/e\\/a\\/www_read_org_cn_16x16.png\"},{\"id\":\"feed\\/http:\\/\\/feeds.feedblitz.com\\/guidingtech\",\"title\":\"Guiding Tech\",\"categories\":[{\"id\":\"user\\/1005907486\\/label\\/Geek\",\"label\":\"Geek\"}],\"sortid\":\"01370A4C\",\"firstitemmsec\":1448529368218418,\"url\":\"http:\\/\\/feeds.feedblitz.com\\/guidingtech\",\"htmlUrl\":\"http:\\/\\/www.guidingtech.com\\/\",\"iconUrl\":\"https:\\/\\/www.inoreader.com\\/cache\\/favicons\\/g\\/u\\/i\\/www_guidingtech_com_16x16.png\"},{\"id\":\"feed\\/http:\\/\\/feed.xbeta.info\\/\",\"title\":\"善用佳软\",\"categories\":[{\"id\":\"user\\/1005907486\\/label\\/软件\",\"label\":\"软件\"}],\"sortid\":\"01370A2A\",\"firstitemmsec\":1448529368218418,\"url\":\"http:\\/\\/feed.xbeta.info\\/\",\"htmlUrl\":\"http:\\/\\/xbeta.info\\/\",\"iconUrl\":\"https:\\/\\/www.inoreader.com\\/cache\\/favicons\\/x\\/b\\/e\\/xbeta_info_48x48.png\"},{\"id\":\"feed\\/http:\\/\\/www.duxieren.com\\/duxieren.xml\",\"title\":\"读写人\",\"categories\":[{\"id\":\"user\\/1005907486\\/label\\/学习\",\"label\":\"学习\"}],\"sortid\":\"01370A51\",\"firstitemmsec\":1448529368218418,\"url\":\"http:\\/\\/www.duxieren.com\\/duxieren.xml\",\"htmlUrl\":\"http:\\/\\/www.duxieren.com\\/\",\"iconUrl\":\"\"},{\"id\":\"feed\\/http:\\/\\/www.idownloadblog.com\\/feed\\/\",\"title\":\"iDownloadBlog.com iDownloadBlog.com\",\"categories\":[{\"id\":\"user\\/1005907486\\/label\\/Apple\",\"label\":\"Apple\"}],\"sortid\":\"019962B6\",\"firstitemmsec\":1448529368218418,\"url\":\"http:\\/\\/www.idownloadblog.com\\/feed\\/\",\"htmlUrl\":\"http:\\/\\/www.idownloadblog.com\\/\",\"iconUrl\":\"https:\\/\\/www.inoreader.com\\/cache\\/favicons\\/i\\/d\\/o\\/www_idownloadblog_com_16x16.png\"},{\"id\":\"feed\\/http:\\/\\/grandenough.blogspot.com\\/feeds\\/posts\\/default\",\"title\":\"Grand enough\",\"categories\":[],\"sortid\":\"01370A53\",\"firstitemmsec\":1448529368218418,\"url\":\"http:\\/\\/grandenough.blogspot.com\\/feeds\\/posts\\/default\",\"htmlUrl\":\"http:\\/\\/grandenough.blogspot.com\\/\",\"iconUrl\":\"https:\\/\\/www.inoreader.com\\/cache\\/favicons\\/g\\/r\\/a\\/grandenough_blogspot_com_16x16.png\"},{\"id\":\"feed\\/http:\\/\\/www.jbguide.me\\/feed\\/\",\"title\":\"越狱指南\",\"categories\":[{\"id\":\"user\\/1005907486\\/label\\/IT\",\"label\":\"IT\"}],\"sortid\":\"01370A3B\",\"firstitemmsec\":1455847200000000,\"url\":\"http:\\/\\/www.jbguide.me\\/feed\\/\",\"htmlUrl\":\"http:\\/\\/jbguide.me\\/\",\"iconUrl\":\"\"},{\"id\":\"feed\\/http:\\/\\/www.appcheers.com\\/feed\",\"title\":\"软件小品\",\"categories\":[{\"id\":\"user\\/1005907486\\/label\\/软件\",\"label\":\"软件\"}],\"sortid\":\"01370A30\",\"firstitemmsec\":1448529368218418,\"url\":\"http:\\/\\/www.appcheers.com\\/feed\",\"htmlUrl\":\"http:\\/\\/www.appcheers.com\\/\",\"iconUrl\":\"https:\\/\\/www.inoreader.com\\/cache\\/favicons\\/a\\/p\\/p\\/www_appcheers_com_64x64.png\"},{\"id\":\"feed\\/http:\\/\\/www.expreview.com\\/rss.php\",\"title\":\"超能网\",\"categories\":[{\"id\":\"user\\/1005907486\\/label\\/IT\",\"label\":\"IT\"}],\"sortid\":\"01370A47\",\"firstitemmsec\":1455847200000000,\"url\":\"http:\\/\\/www.expreview.com\\/rss.php\",\"htmlUrl\":\"http:\\/\\/www.expreview.com\\/\",\"iconUrl\":\"https:\\/\\/www.inoreader.com\\/cache\\/favicons\\/e\\/x\\/p\\/www_expreview_com_16x16.png\"},{\"id\":\"feed\\/http:\\/\\/mrreaderblog.curioustimes.de\\/rss\",\"title\":\"Mr. Reader Blog\",\"categories\":[],\"sortid\":\"01370A52\",\"firstitemmsec\":1448529368218418,\"url\":\"http:\\/\\/mrreaderblog.curioustimes.de\\/rss\",\"htmlUrl\":\"http:\\/\\/mrreaderblog.curioustimes.de\\/\",\"iconUrl\":\"https:\\/\\/www.inoreader.com\\/cache\\/favicons\\/m\\/r\\/r\\/mrreaderblog_curioustimes_de_16x16.png\"},{\"id\":\"feed\\/http:\\/\\/macshuo.com\\/?feed=rss2\",\"title\":\"MacTalk-池建强的随想录\",\"categories\":[{\"id\":\"user\\/1005907486\\/label\\/Geek\",\"label\":\"Geek\"}],\"sortid\":\"01CC803A\",\"firstitemmsec\":1455700956330835,\"url\":\"http:\\/\\/macshuo.com\\/?feed=rss2\",\"htmlUrl\":\"http:\\/\\/macshuo.com\\/\",\"iconUrl\":\"https:\\/\\/www.inoreader.com\\/cache\\/favicons\\/m\\/a\\/c\\/macshuo_com_16x16.png\"},{\"id\":\"feed\\/http:\\/\\/feeds.feedburner.com\\/zhihu-daily\",\"title\":\"知乎日报\",\"categories\":[{\"id\":\"user\\/1005907486\\/label\\/生活\",\"label\":\"生活\"}],\"sortid\":\"0198AF7B\",\"firstitemmsec\":1448529368218418,\"url\":\"http:\\/\\/feeds.feedburner.com\\/zhihu-daily\",\"htmlUrl\":\"http:\\/\\/daily.zhihu.com\\/\",\"iconUrl\":\"https:\\/\\/www.inoreader.com\\/cache\\/favicons\\/d\\/a\\/i\\/daily_zhihu_com_16x16.png\"},{\"id\":\"feed\\/http:\\/\\/www.zdfans.com\\/feed\",\"title\":\"zd423\",\"categories\":[{\"id\":\"user\\/1005907486\\/label\\/软件\",\"label\":\"软件\"}],\"sortid\":\"01370A2F\",\"firstitemmsec\":1448529368218418,\"url\":\"http:\\/\\/www.zdfans.com\\/feed\",\"htmlUrl\":\"http:\\/\\/www.zdfans.com\\/\",\"iconUrl\":\"https:\\/\\/www.inoreader.com\\/cache\\/favicons\\/z\\/d\\/f\\/www_zdfans_com_16x16.png\"},{\"id\":\"feed\\/http:\\/\\/sspai.com\\/feed\",\"title\":\"少数派\",\"categories\":[{\"id\":\"user\\/1005907486\\/label\\/IT\",\"label\":\"IT\"}],\"sortid\":\"013BE18C\",\"firstitemmsec\":1455847200000000,\"url\":\"http:\\/\\/sspai.com\\/feed\",\"htmlUrl\":\"http:\\/\\/sspai.com\\/feed\",\"iconUrl\":\"https:\\/\\/www.inoreader.com\\/cache\\/favicons\\/s\\/s\\/p\\/sspai_com_16x16.png\"},{\"id\":\"feed\\/http:\\/\\/pansci.asia\\/feed\",\"title\":\"PanSci 泛科學\",\"categories\":[{\"id\":\"user\\/1005907486\\/label\\/学习\",\"label\":\"学习\"}],\"sortid\":\"0198AF95\",\"firstitemmsec\":1448529368218418,\"url\":\"http:\\/\\/pansci.asia\\/feed\",\"htmlUrl\":\"http:\\/\\/pansci.asia\\/\",\"iconUrl\":\"https:\\/\\/www.inoreader.com\\/cache\\/favicons\\/p\\/a\\/n\\/pansci_asia_16x16.png\"}]}\n";
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
