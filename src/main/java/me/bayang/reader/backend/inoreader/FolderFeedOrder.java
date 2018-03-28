package me.bayang.reader.backend.inoreader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.scene.image.Image;
import me.bayang.reader.backend.UserInfo;
import me.bayang.reader.rssmodels.Categories;
import me.bayang.reader.rssmodels.Feed;
import me.bayang.reader.rssmodels.FoldersTagsList;
import me.bayang.reader.rssmodels.Subscription;
import me.bayang.reader.rssmodels.SubscriptionsList;
import me.bayang.reader.rssmodels.Tag;

/**
 * This class is used to get the order of the folder and the feed inside.
 */
@Service
public class FolderFeedOrder {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FolderFeedOrder.class);
    
    @Autowired
    private ConnectServer connectServer;
    
    @Autowired
    private ObjectMapper mapper;
    
    public static Map<String, Image> iconMap;

    /**
     * Connect server and get the information about the order, then return a map.
     * UNUSED METHOD
     *
     * @return A map containing order (inoreader order).
     */
    @Deprecated
    public Map<Feed, List<Subscription>> getInoreaderOrder() {
        String userId = UserInfo.getUserId();
        //get the streamprefs
        String streamprefs = null;
        try (BufferedReader reader = connectServer.connectServer(ConnectServer.streamPreferenceListURL)) {
            streamprefs = reader.readLine();
            if (reader != null) {
                reader.close();
            }
        } catch (IOException ioe) {
            LOGGER.error("error while getting inoreadr order",ioe);
        }

        //parse the streamprefs and get the folder order
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(streamprefs);
        JsonObject object = element.getAsJsonObject();
        JsonArray root = object.getAsJsonObject("streamprefs").getAsJsonArray("user/" + userId + "/state/com.google/root");
        String folderOrderString = root.get(0).getAsJsonObject().get("value").getAsString();
        List<String> folderOrderList = sortOrder(folderOrderString);

        //get the folderTagList and the subscriptionList
        Gson gson = new Gson();
        FoldersTagsList foldersTagsList = gson.fromJson(connectServer.connectServer(ConnectServer.folderTagListURL), FoldersTagsList.class);
        LOGGER.debug("foldersTagsList -> {}", foldersTagsList);
        SubscriptionsList subscriptionsList = gson.fromJson(connectServer.connectServer(ConnectServer.subscriptionListURL), SubscriptionsList.class);
        LOGGER.debug("subscriptionsList -> {}", subscriptionsList);
        //get the icon map
        iconMap = new HashMap<>();
        for (Subscription subscription : subscriptionsList.getSubscriptions()) {
            if (!subscription.getIconUrl().equals("")) {
                if (! iconMap.containsKey(subscription.getId())) {
                    iconMap.put(subscription.getId(), new Image(subscription.getIconUrl(), true));
                }
            }
        }
        //sort the folder order
        Map<Feed, List<Subscription>> orderMap = new LinkedHashMap<>();
        orderMap.put(new Tag("All Items", null), null);
        orderMap.put(foldersTagsList.getTags().get(0), null);
        for (String s : folderOrderList) {
            boolean isFolder = false;
            for (Tag tag : foldersTagsList.getTags()) {
                if (s.equals(tag.getSortid())) {
                    isFolder = true;

                    //get the feed order in the folder
                    List<Subscription> list = getFeedOrder(object, tag.getId(), subscriptionsList);
                    orderMap.put(tag, list);
                    break;
                }
            }
            if (!isFolder) {
                for (Subscription subscription : subscriptionsList.getSubscriptions()) {
                    if (s.equals(subscription.getSortid())) {
                        orderMap.put(subscription, null);
                        break;
                    }
                }
            }
        }
        return orderMap;
    }
    
    public Map<Feed, List<Subscription>> getAlphabeticalOrder() {
        try {
            //get the folderTagList and the subscriptionList
            BufferedReader folderReader = connectServer.connectServer(ConnectServer.folderTagListURL);
            FoldersTagsList foldersTagsList = mapper.readValue(folderReader, FoldersTagsList.class);
//        LOGGER.debug("foldersTagsList -> {}", foldersTagsList);
            BufferedReader subscriptionsReader = connectServer.connectServer(ConnectServer.subscriptionListURL);
            SubscriptionsList subscriptionsList = mapper.readValue(subscriptionsReader, SubscriptionsList.class);
//        LOGGER.debug("subscriptionsList -> {}", subscriptionsList);
            ConnectServer.closeReader(folderReader);
            ConnectServer.closeReader(subscriptionsReader);
            
            Comparator<Feed> labelComp = new Comparator<Feed>() {
                @Override
                public int compare(Feed o1, Feed o2) {
                    if (o1 instanceof Categories && o2 instanceof Subscription) {
                        return -1;
                    } 
                    else if (o1 instanceof Subscription && o2 instanceof Categories) {
                        return 1;
                    }
                    else {
                        return o1.getLabel().compareToIgnoreCase(o2.getLabel());
                    }
                }
            };
            Map<Feed, List<Subscription>> treeMap = new TreeMap<>(labelComp);
            
            //get the icon map
            iconMap = new HashMap<>();
            //sort the folder order
            Map<Feed, List<Subscription>> orderMap = new LinkedHashMap<>();
            orderMap.put(new Categories("All Items", "All Items"), null);
            orderMap.put(new Categories(foldersTagsList.getTags().get(0).getId(), "starred"), null);
            for (Subscription subscription : subscriptionsList.getSubscriptions()) {
                if (!subscription.getIconUrl().equals("")) {
                    if (! iconMap.containsKey(subscription.getId())) {
                        iconMap.put(subscription.getId(), new Image(subscription.getIconUrl(), true));
                    }
                }
                if (subscription.getCategories() != null && ! subscription.getCategories().isEmpty()) {
                    for (Categories c : subscription.getCategories()) {
                        if (! treeMap.containsKey(c)) {
                            List<Subscription> l = new ArrayList<>();
                            l.add(subscription);
                            treeMap.put(c, l);
                        }
                        else {
                            treeMap.get(c).add(subscription);
                        }
                    }
                }
                else {
                    treeMap.put(subscription, null);
                }
            }
            
            Comparator<Subscription> comp = new Comparator<Subscription>() {
                @Override
                public int compare(Subscription o1, Subscription o2) {
                    return o1.getTitle().compareToIgnoreCase(o2.getTitle());
                }
            };
            for (List<Subscription> subs : treeMap.values()) {
                if (subs != null && ! subs.isEmpty()) {
                    Collections.sort(subs, comp);
                }
            }
            for (Map.Entry<Feed, List<Subscription>> entry : treeMap.entrySet()) {
                    orderMap.put(entry.getKey(), entry.getValue());
            }
            return orderMap;
        } catch (IOException e) {
            LOGGER.error("error while loading folder & feed order", e);
        }
        return Collections.emptyMap();
    }

    private static List<String> sortOrder(String s) { //maybe using stream
        List<String> list = new ArrayList<>();
        for (int i = 0; i < s.length(); i += 8) {
            list.add(s.substring(i, i + 8));
        }
        return list;
    }

    private static List<Subscription> getFeedOrder(JsonObject streamprefs, String folderId, SubscriptionsList subscriptionsList) {
        String feedOrderString = streamprefs.getAsJsonObject("streamprefs").getAsJsonArray(folderId).get(1).getAsJsonObject().get("value").getAsString();
        List<String> feedOrderList = sortOrder(feedOrderString);
        List<Subscription> list = new ArrayList<>();             //the list to return, contains the feed order
        for (String s : feedOrderList) {
            for (Subscription subscription : subscriptionsList.getSubscriptions()) {
                if (s.equals(subscription.getSortid())) {
                    list.add(subscription);
                }
            }
        }
        return list;
    }

}
