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
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

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
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.concurrent.Task;
import me.bayang.reader.controllers.RssController;
import me.bayang.reader.rssmodels.Item;
import me.bayang.reader.rssmodels.StreamContent;
import me.bayang.reader.rssmodels.UnreadCounter;
import me.bayang.reader.rssmodels.UnreadCounts;
import me.bayang.reader.rssmodels.UserInfo;
import me.bayang.reader.rssmodels.UserInformation;
import me.bayang.reader.storage.IStorageService;
import okhttp3.HttpUrl;
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
    
    @Resource(name = "threadPoolTaskExecutor")
    ThreadPoolTaskExecutor taskExecutor;
    
    private ResourceBundle bundle = ResourceBundle.getBundle("i18n.translations");
    
    private Map<String, String> continuationsMap = new HashMap<>();
    
    public static final String API_BASE_URL = "https://www.inoreader.com/reader/api/0";
    
    //URLs for InoReader
    public static String userinfoURL = API_BASE_URL + "/user-info";
    public static String unreadCountURL = API_BASE_URL + "/unread-count";
    public static String subscriptionListURL = API_BASE_URL + "/subscription/list";
    public static String folderTagListURL = API_BASE_URL + "/tag/list";
    public static String streamContentURL = API_BASE_URL + "/stream/contents/user/-/state/com.google/root?xt=user/-/state/com.google/read&n=100";
    public static String starredContentURL = API_BASE_URL + "/stream/contents/user/-/state/com.google/starred?n=100";
    public static String streamPreferenceListURL = API_BASE_URL + "/preference/stream/list";
    public static String itemIDsURL = API_BASE_URL + "/stream/items/ids?xt=user/-/state/com.google/read";
    
    public static String markAllReadURL = API_BASE_URL + "/mark-all-as-read";
    
    public static String markFeedReadURL = API_BASE_URL + "/edit-tag?a=user/-/state/com.google/read";
    
    public static String markStarredURL = API_BASE_URL + "/edit-tag?a=user/-/state/com.google/starred";
    
    public static String markUnstarredURL = API_BASE_URL + "/edit-tag?r=user/-/state/com.google/starred";
    
    public static String editSubscriptionURL = API_BASE_URL + "/subscription/edit";
    
    public static String addSubscriptionURL = API_BASE_URL + "/subscription/quickadd";
    
    public static final String baseStreamContentURL = API_BASE_URL + "/stream/contents";
    
    public static final HttpUrl baseStreamContentHTTPUrl = HttpUrl.parse(baseStreamContentURL);
    
    public static final String OAUTH_URL = "https://www.inoreader.com/oauth2/auth";
    public static final String TOKEN_URL = "https://www.inoreader.com/oauth2/token";
    
    public static final String OAUTH_REDIRECT_URL = "http://localhost:8080/reader/redirect";
    
    public static final String readTag = "user/%s/state/com.google/read";
    
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
                URI.create(OAUTH_URL), 
                URI.create(TOKEN_URL), 
                new Duration(1, 0, 20, 0, 0));
        credentials = new BasicOAuth2ClientCredentials(
                getAppId(), getAppKey());
        client = new BasicOAuth2Client(
                provider,
                credentials,
                new LazyUri(new Precoded(OAUTH_REDIRECT_URL)));
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
            if (userInfo != null) {
                UserInfo.setUserId(userInfo.getUserId());
                storage.saveUser(userInfo);
            }
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
        Response response = null;
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
            response = getOkClient().newCall(request).execute();
            
            if (response.isSuccessful()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().source().inputStream(), "utf-8"));
                return reader;
            } else {
                RssController.snackbarNotify(bundle.getString("connectionFailure"));
                response.close();
                LOGGER.debug("connectServer error code {} for {}",response.code(), request.url());
            }

        } catch (UnknownHostException uhe) {
            RssController.snackbarNotify(bundle.getString("connectionFailure"));
            LOGGER.error("connectServer error", uhe);
        } catch (IOException ioe) {
            RssController.snackbarNotify(bundle.getString("connectionFailure"));
            LOGGER.error("connectServer error", ioe);
            if (response != null) {
                response.close();
            }
        } catch (ProtocolException e) {
            RssController.snackbarNotify(bundle.getString("connectionFailure"));
            LOGGER.error("connectServer error", e);
        }
        return null;
    }
    
    /**
     * Connect the server and get the Reader.
     * @param url the related URL in the field.
     * @return the BufferedReader which can be used by Gson to get information.
     */
    public BufferedReader connectServer(HttpUrl url) {
        Response response = null;
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
            response = getOkClient().newCall(request).execute();
            
            if (response.isSuccessful()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().source().inputStream(), "utf-8"));
                return reader;
            } else {
                RssController.snackbarNotify(bundle.getString("connectionFailure"));
                response.close();
                LOGGER.debug("connectServer error code {} for {}",response.code(), request.url());
            }

        } catch (UnknownHostException uhe) {
            RssController.snackbarNotify(bundle.getString("connectionFailure"));
            LOGGER.error("connectServer error", uhe);
        } catch (IOException ioe) {
            RssController.snackbarNotify(bundle.getString("connectionFailure"));
            LOGGER.error("connectServer error", ioe);
            if (response != null) {
                response.close();
            }
        } catch (ProtocolException e) {
            RssController.snackbarNotify(bundle.getString("connectionFailure"));
            LOGGER.error("connectServer error", e);
        }
        return null;
    }
    
    public Request getRequest(HttpUrl url) {
        try {
            if (! tokenIsStillValid()) {
                refreshToken();
            }
            String authorization;
                authorization = String.format("Bearer %s", token.accessToken());
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", authorization)
                    .build();
            return request;
        } catch (ProtocolException e) {
            RssController.snackbarNotify(bundle.getString("connectionFailure"));
            LOGGER.error("getRequest error", e);
        }
        return null;
    }
    
    @Async("threadPoolTaskExecutor")
    public void markAsRead(String decimalId) {
        HttpUrl URL = HttpUrl.parse(markFeedReadURL);
        BufferedReader reader = connectServer(URL.newBuilder()
                .setQueryParameter("i", decimalId)
                .build());
        closeReader(reader);
    }
    
    @Async("threadPoolTaskExecutor")
    public void markAllAsRead(long timestamp, String streamId) {
        HttpUrl URL = HttpUrl.parse(markAllReadURL);
        BufferedReader reader = connectServer(URL.newBuilder()
                .setQueryParameter("ts", String.valueOf(timestamp))
                .setQueryParameter("s", streamId)
                .build());
        closeReader(reader);
    }
    
    @Async("threadPoolTaskExecutor")
    public void unsubscribe(String feedId) {
        HttpUrl URL = HttpUrl.parse(editSubscriptionURL);
        BufferedReader reader = connectServer(URL.newBuilder()
                .setQueryParameter("ac", "unsubscribe")
                .setQueryParameter("s", feedId)
                .build());
        closeReader(reader);
    }
    
    @Async("threadPoolTaskExecutor")
    public void removeFromFolder(String feedId, String folder) {
        HttpUrl URL = HttpUrl.parse(editSubscriptionURL);
        BufferedReader reader = connectServer(URL.newBuilder()
                .setQueryParameter("ac", "edit")
                .setQueryParameter("s", feedId)
                .setQueryParameter("r", folder)
                .build());
        closeReader(reader);
    }
    
    @Async("threadPoolTaskExecutor")
    public void star(String decimalId) {
        HttpUrl URL = HttpUrl.parse(markStarredURL);
        BufferedReader reader = connectServer(URL.newBuilder()
                .setQueryParameter("i", decimalId)
                .build());
        closeReader(reader);
    }
    
    @Async("threadPoolTaskExecutor")
    public void unStar(String decimalId) {
        HttpUrl URL = HttpUrl.parse(markUnstarredURL);
        BufferedReader reader = connectServer(URL.newBuilder()
                .setQueryParameter("i", decimalId)
                .build());
        closeReader(reader);
    }
    
    public Map<String,Integer> getUnreadCountsMap() {
        BufferedReader counterReader = null;
        try {
            HashMap<String, Integer> map = new HashMap<>();
            counterReader = connectServer(ConnectServer.unreadCountURL);
            UnreadCounter counter = mapper.readValue(counterReader, UnreadCounter.class);
            List<UnreadCounts> counts = counter.getUnreadcounts();
            for (UnreadCounts count : counts) {
                map.put(count.getId(), count.getCount());
            }
            map.put("All Items", counts.get(0).getCount());
            closeReader(counterReader);
            return map;
        } catch (IOException e) {
            RssController.snackbarNotify(bundle.getString("connectionFailure"));
            LOGGER.error("getUnreadCountMap error", e);
        }
        return Collections.emptyMap();
    }
    
    public List<Item> getStreamContent(String URLString) {
        BufferedReader reader = null;
        try {
            reader = connectServer(URLString);
            StreamContent content;
            content = mapper.readValue(reader, StreamContent.class);
            List<Item> itemList = new ArrayList<>(content.getItems());
    
            while (content.getContinuation() != null) {
                reader = connectServer(URLString + "&c=" + content.getContinuation());
                content = mapper.readValue(reader, StreamContent.class);
                itemList.addAll(content.getItems());
            }
            closeReader(reader);
            return itemList;
        } catch (IOException e1) {
            RssController.snackbarNotify(bundle.getString("connectionFailure"));
            LOGGER.error("getStreamContent error", e1);
        }
        return Collections.emptyList();
    }
    
    public List<Item> getOlderStreamContent(String streamId) {
        BufferedReader reader = null;
        try {
            HttpUrl urlWithParams;
            // we already started to fetch old content, so start from where we stopped
            if (continuationsMap.containsKey(streamId)) {
                urlWithParams = baseStreamContentHTTPUrl.newBuilder()
                        .addPathSegment(streamId)
                        .setQueryParameter("n", "50")
                        .setQueryParameter("c", continuationsMap.get(streamId))
                        .build();
            }
            // first request to fetch
            else {
                urlWithParams = baseStreamContentHTTPUrl.newBuilder()
                        .addPathSegment(streamId)
                        .setQueryParameter("n", "50")
                        .build();
            }
            reader = connectServer(urlWithParams);
            StreamContent content;
            content = mapper.readValue(reader, StreamContent.class);
            LOGGER.debug("{}",content.getItems().size());
            List<Item> itemList = new ArrayList<>();
            for (Item item : content.getItems()) {
                if (UserInfo.getUserId() != null && ! UserInfo.getUserId().isEmpty()) {
                    if (item.getCategories().contains("user/-/state/com.google/read") 
                            || item.getCategories().contains(String.format(readTag, UserInfo.getUserId()))) {
                        item.setRead(true);
                        itemList.add(item);
                    }
                }
                else {
                    if (item.getCategories().contains("user/-/state/com.google/read")) {
                        item.setRead(true);
                        itemList.add(item);
                    }
                }
            }
            LOGGER.debug("{}",itemList.size());
            if (content.getContinuation() != null && ! content.getContinuation().isEmpty()) {
                LOGGER.debug("caching stream {} : continuation {}", streamId, content.getContinuation());
                continuationsMap.put(streamId, content.getContinuation());
            }
            closeReader(reader);
            LOGGER.debug("{}",itemList);
            return itemList;
        } catch (IOException e1) {
            RssController.snackbarNotify(bundle.getString("connectionFailure"));
            LOGGER.error("getStreamContent error", e1);
        }
        return Collections.emptyList();
    }
    
    public Task<List<Item>> getOlderreadItemsTask(String streamId) {
        Task<List<Item>> t = new Task<List<Item>>() {
            @Override
            protected List<Item> call() throws Exception {
                LOGGER.debug("start getOlderreadItemsTask");
                return getOlderStreamContent(streamId);
            }
        };
        return t;
    }
    
    private void refreshToken() {
        OAuth2AccessToken newToken;
        try {
            newToken = new TokenRefreshGrant(client, token).accessToken(executor);
            setToken(newToken);
        } catch (IOException | ProtocolError | ProtocolException e) {
            RssController.snackbarNotify(bundle.getString("connectionFailure"));
            LOGGER.error("refreshToken error", e);
        }
    }
    
    public boolean tokenIsStillValid() {
        try {
            if (! this.token.expirationDate().before(DateTime.nowAndHere())) {
                return true;
            }
        } catch (ProtocolException e) {
            RssController.snackbarNotify(bundle.getString("connectionFailure"));
            LOGGER.error("tokenIsStillValid error", e);
            return false;
        }
        return false;
    }
    
    @Async("threadPoolTaskExecutor")
    public void fetchAndSaveUSer() {
        UserInformation userInfo = getUserInformation();
        UserInfo.setUserId(userInfo.getUserId());
        storage.saveUser(userInfo);
    }
    
    public UserInformation getUserInformation() {
        BufferedReader reader = null;
        try {
            reader = connectServer(ConnectServer.userinfoURL);
            UserInformation userInformation;
            userInformation = mapper.readValue(reader, UserInformation.class);
            closeReader(reader);
            return userInformation;
        } catch (IOException e) {
            RssController.snackbarNotify(bundle.getString("connectionFailure"));
            LOGGER.error("getUserInformation error", e);
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
            RssController.snackbarNotify(bundle.getString("connectionFailure"));
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

    public ThreadPoolTaskExecutor getTaskExecutor() {
        return taskExecutor;
    }

    public void setTaskExecutor(ThreadPoolTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public Map<String, String> getContinuationsMap() {
        return continuationsMap;
    }

    public void setContinuationsMap(Map<String, String> continuationsMap) {
        this.continuationsMap = continuationsMap;
    }
    
    

}