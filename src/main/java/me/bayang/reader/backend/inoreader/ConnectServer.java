package me.bayang.reader.backend.inoreader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.dmfs.httpessentials.client.HttpRequestExecutor;
import org.dmfs.httpessentials.exceptions.ProtocolError;
import org.dmfs.httpessentials.exceptions.ProtocolException;
import org.dmfs.httpessentials.okhttp.OkHttpExecutor;
import org.dmfs.jems.single.Single;
import org.dmfs.jems.single.elementary.ValueSingle;
import org.dmfs.oauth2.client.BasicOAuth2AuthorizationProvider;
import org.dmfs.oauth2.client.BasicOAuth2Client;
import org.dmfs.oauth2.client.BasicOAuth2ClientCredentials;
import org.dmfs.oauth2.client.OAuth2AccessToken;
import org.dmfs.oauth2.client.OAuth2AuthorizationProvider;
import org.dmfs.oauth2.client.OAuth2Client;
import org.dmfs.oauth2.client.OAuth2ClientCredentials;
import org.dmfs.oauth2.client.OAuth2InteractiveGrant;
import org.dmfs.oauth2.client.grants.AuthorizationCodeGrant;
import org.dmfs.oauth2.client.grants.TokenRefreshGrant;
import org.dmfs.oauth2.client.scope.BasicScope;
import org.dmfs.rfc3986.encoding.Precoded;
import org.dmfs.rfc3986.uris.LazyUri;
import org.dmfs.rfc5545.DateTime;
import org.dmfs.rfc5545.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.scene.control.Alert;
import me.bayang.reader.rssmodels.Item;
import me.bayang.reader.rssmodels.StreamContent;
import me.bayang.reader.rssmodels.UnreadCounter;
import me.bayang.reader.rssmodels.UnreadCounts;
import me.bayang.reader.rssmodels.UserInfo;
import me.bayang.reader.rssmodels.UserInformation;
import me.bayang.reader.storage.IStorageService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This class provide functions to connect the server and get the related Reader.
 */
@Service
public class ConnectServer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectServer.class);
    
    private OkHttpClient okClient;
    private HttpRequestExecutor executor;
    private OAuth2AuthorizationProvider provider;
    private OAuth2ClientCredentials credentials;
    private OAuth2Client client;
    private OAuth2InteractiveGrant grant;
    private URI authorizationUrl;
    private OAuth2AccessToken token;
    private boolean shouldAskPermissionOrLogin = true;
    
    @Value("${inoreader.appId}")
    public String AppId;
    
    @Value("${inoreader.appKey}")
    public String AppKey;
    
    @Autowired
    private IStorageService storage;
    
    @Autowired
    private ObjectMapper mapper;
    
    public static final String API_BASE_URL = "https://www.inoreader.com/reader/api/0";
    
    //URLs for InoReader
    public static String userinfoURL = API_BASE_URL + "/user-info";
    public static String loginURL = "https://www.inoreader.com/accounts/ClientLogin?";
    public static String unreadCountURL = API_BASE_URL + "/unread-count";
    public static String subscriptionListURL = API_BASE_URL + "/subscription/list";
    public static String folderTagListURL = API_BASE_URL + "/tag/list";
    public static String streamContentURL = API_BASE_URL + "/stream/contents/user/-/state/com.google/root?xt=user/-/state/com.google/read&n=100";
    public static String starredContentURL = API_BASE_URL + "/stream/contents/user/-/state/com.google/starred?n=100";
    public static String streamPreferenceListURL = API_BASE_URL + "/preference/stream/list";
    public static String itemIDsURL = API_BASE_URL + "/stream/items/ids?xt=user/-/state/com.google/read";
    public static String markAllReadURL = API_BASE_URL + "/mark-all-as-read?ts=";
    public static String markFeedReadURL = API_BASE_URL + "/edit-tag?a=user/-/state/com.google/read&i=";
    public static String markStarredURL = API_BASE_URL + "/edit-tag?a=user/-/state/com.google/starred&i=";
    public static String markUnstarredURL = API_BASE_URL + "/edit-tag?r=user/-/state/com.google/starred&i=";
    public static String editSubscriptionURL = API_BASE_URL + "/subscription/edit?";
    public static String addSubscriptionURL = API_BASE_URL + "/subscription/quickadd?quickadd=";
    
    @PostConstruct
    public void initHttpClient() throws IOException, ProtocolError, ProtocolException {
        if (!storage.hasToken()) {
            setShouldAskPermissionOrLogin(true);
        } else {
            token = storage.loadToken();
            if (token != null) {
                setShouldAskPermissionOrLogin(false);
            }
        }
        okClient = new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .readTimeout(1, TimeUnit.MINUTES)
                .connectTimeout(1, TimeUnit.MINUTES)
                .build();
        Single<OkHttpClient> s = new ValueSingle<OkHttpClient>(okClient);
        executor = new OkHttpExecutor(s);
        provider = new BasicOAuth2AuthorizationProvider(
                URI.create("https://www.inoreader.com/oauth2/auth"), 
                URI.create("https://www.inoreader.com/oauth2/token"), 
                new Duration(1, 0, 20, 0, 0));
        credentials = new BasicOAuth2ClientCredentials(
                getAppId(), getAppKey());
        client = new BasicOAuth2Client(
                provider,
                credentials,
                new LazyUri(new Precoded("http://localhost:8080/reader/redirect")));
        grant = new AuthorizationCodeGrant(
                client, new BasicScope("read write"));
        authorizationUrl = grant.authorizationUrl();
        LOGGER.debug("auth url = {}", authorizationUrl.toString());
        if (! isShouldAskPermissionOrLogin()) {
            refreshToken();
        }
        String userId = storage.loadUser();
        if (userId == null || userId.isEmpty()) {
            UserInformation userInfo = getUserInformation();
            UserInfo.setUserId(userInfo.getUserId());
            storage.saveUser(userInfo);
        }
        else {
            UserInfo.setUserId(userId);
        }
    }

    /**
     * Connect the server and get the Reader.
     * @param url the related URL in the field.
     * @return the BufferedReader which can be used by Gson to get information.
     */
    public BufferedReader connectServer(String url) {
        try {
            if (! tokenIsStillValid()) {
                refreshToken();
            }
            String authorization = String.format("Bearer %s", token.accessToken());
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", authorization)
                    .build();
            // FIXME passer en asynchrone
            Response response = getOkClient().newCall(request).execute();
            
            if (response.isSuccessful()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().source().inputStream(), "utf-8"));
                return reader;
            } else {
                LOGGER.debug("error code {}",response.code());
                // FIXME renvoyer un message a ucontroller pour affichage
            }

        } catch (UnknownHostException uhe) {
            new Alert(Alert.AlertType.ERROR,uhe.getMessage()).show();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    //connectServer.connectServer(ConnectServer.markFeedReadURL + newValue.getDecimalId())
    @Async("threadPoolTaskExecutor")
    public void markAsRead(String decimalId) {
        BufferedReader reader = connectServer(markFeedReadURL + decimalId);
        closeReader(reader);
    }
    
    //connectServer.connectServer(ConnectServer.markAllReadURL + lastUpdateTime.getEpochSecond() + "&s=" + treeView.getSelectionModel().getSelectedItem().getValue().getId());
    @Async("threadPoolTaskExecutor")
    public void markAllAsRead(long timestamp, String streamId) {
        BufferedReader reader = connectServer(markAllReadURL + timestamp + "&s=" + streamId);
        closeReader(reader);
    }
    
    //connectServer.connectServer(ConnectServer.editSubscriptionURL + "ac=unsubscribe&s=" + treeView.getSelectionModel().getSelectedItem().getValue().getId());
    @Async("threadPoolTaskExecutor")
    public void unsubscribe(String feedId) {
        BufferedReader reader = connectServer(ConnectServer.editSubscriptionURL + "ac=unsubscribe&s=" + feedId);
        closeReader(reader);
    }
    
    public Map<String,Integer> getUnreadCountsMap() {
        try {
        HashMap<String, Integer> map = new HashMap<>();
        BufferedReader counterReader = connectServer(ConnectServer.unreadCountURL);
        UnreadCounter counter = mapper.readValue(counterReader, UnreadCounter.class);
//        UnreadCounter counter = gson.fromJson(counterReader, UnreadCounter.class);
        List<UnreadCounts> counts = counter.getUnreadcounts();
        for (UnreadCounts count : counts) {
            map.put(count.getId(), count.getCount());
        }
        map.put("All Items", counts.get(0).getCount());
        closeReader(counterReader);
        return map;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }
    
    public List<Item> getStreamContent(String URLString) {
        try {
        BufferedReader reader = connectServer(URLString);
//        StreamContent content = gson.fromJson(reader, StreamContent.class);
        StreamContent content;
        content = mapper.readValue(reader, StreamContent.class);
        List<Item> itemList = new ArrayList<>(content.getItems());

        while (content.getContinuation() != null) {
            reader = connectServer(URLString + "&c=" + content.getContinuation());
//            content = gson.fromJson(reader, StreamContent.class);
            content = mapper.readValue(reader, StreamContent.class);
            itemList.addAll(content.getItems());
        }
        closeReader(reader);
        return itemList;
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return Collections.emptyList();
    }
    
    private void refreshToken() {
        OAuth2AccessToken newToken;
        try {
            newToken = new TokenRefreshGrant(client, token).accessToken(executor);
            setToken(newToken);
        } catch (IOException | ProtocolError | ProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public boolean tokenIsStillValid() {
        try {
            if (! this.token.expirationDate().before(DateTime.nowAndHere())) {
                return true;
            }
        } catch (ProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return false;
    }
    
    public UserInformation getUserInformation() {
        try {
            BufferedReader reader = connectServer(ConnectServer.userinfoURL);
            UserInformation userInformation;
            userInformation = mapper.readValue(reader, UserInformation.class);
            closeReader(reader);
            return userInformation;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    public static void closeReader(BufferedReader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                /* noop */
            }
        }
    }
    
    public String getAppId() {
        return AppId;
    }

    public void setAppId(String appId) {
        AppId = appId;
    }

    public String getAppKey() {
        return AppKey;
    }

    public void setAppKey(String appKey) {
        AppKey = appKey;
    }

    public OAuth2InteractiveGrant getGrant() {
        return grant;
    }

    public void setGrant(OAuth2InteractiveGrant grant) {
        this.grant = grant;
    }

    public URI getAuthorizationUrl() {
        return authorizationUrl;
    }

    public void setAuthorizationUrl(URI authorizationUrl) {
        this.authorizationUrl = authorizationUrl;
    }

    public HttpRequestExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(HttpRequestExecutor executor) {
        this.executor = executor;
    }

    public OAuth2AccessToken getToken() {
        return token;
    }

    public void setToken(OAuth2AccessToken token) {
        this.token = token;
        try {
            storage.saveToken(token);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            LOGGER.error("failed to save token {}", token, e);
        }
    }

    public OkHttpClient getOkClient() {
        return okClient;
    }

    public void setOkClient(OkHttpClient okClient) {
        this.okClient = okClient;
    }

    public boolean isShouldAskPermissionOrLogin() {
        return shouldAskPermissionOrLogin;
    }

    public void setShouldAskPermissionOrLogin(boolean shouldAskPermissionOrLogin) {
        this.shouldAskPermissionOrLogin = shouldAskPermissionOrLogin;
    }

}