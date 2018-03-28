package me.bayang.reader.share.pocket;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.bayang.reader.storage.IStorageService;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class PocketClient {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PocketClient.class);
    
    @Value("${pocket.key}")
    private String key;
    
    @Resource(name="mapper")
    private ObjectMapper mapper;
    
    @Autowired
    private IStorageService storage;
    
    private static final String requestTokenUrl = "https://getpocket.com/v3/oauth/request";
    public static final HttpUrl requestTokenHTTPUrl = HttpUrl.parse(requestTokenUrl);
    
    private static final String redirectUrl = "http://localhost:8080/pocket/reader/redirect";
    public static final String redirectUrlNoScheme = "localhost:8080/pocket/reader/redirect";
    
    // https://getpocket.com/auth/authorize?request_token=YOUR_REQUEST_TOKEN&redirect_uri=YOUR_REDIRECT_URI
    private static final String authorizeUrl = "https://getpocket.com/auth/authorize";
    public static final HttpUrl authorizeHTTPUrl = HttpUrl.parse(authorizeUrl);
    
    private static final HttpUrl AccessTokenUrl = HttpUrl.parse("https://getpocket.com/v3/oauth/authorize");
    
    private static final HttpUrl addLinkUrl = HttpUrl.parse("https://getpocket.com/v3/add");
    
    private OkHttpClient httpClient;
    
    private RequestBody requestTokenBody;
    
    private String code;
    
    private String accessToken;
    
    private String username;
    
    @PostConstruct
    public void initialize() throws JsonProcessingException {
        httpClient = new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .readTimeout(1, TimeUnit.MINUTES)
                .connectTimeout(1, TimeUnit.MINUTES)
                .build();
        PocketTokenRequestPayload payload = new PocketTokenRequestPayload();
        payload.setConsumerKey(key);
        payload.setRedirectUri(redirectUrl);
        requestTokenBody = RequestBody.create(MediaType.parse("application/json"), mapper.writeValueAsString(payload));
    }
    
    public void addLink(String link, String tags) {
        PocketAddLinkPayload p = new PocketAddLinkPayload();
        p.setAccessToken(accessToken);
        p.setConsumerKey(key);
        p.setUrl(link);
        if (tags != null && ! tags.isEmpty()) {
            p.setTags(tags);
        }
        try {
            RequestBody r = RequestBody.create(MediaType.parse("application/json"), mapper.writeValueAsString(p));
            Request request = new Request.Builder()
                    .url(addLinkUrl)
                    .addHeader("Content-Type", "application/json; charset=UTF-8")
                    .addHeader("X-Accept", "application/json")
                    .post(r)
                    .build();
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public Response getRequestToken() throws IOException {
        Request request = new Request.Builder()
                .url(requestTokenHTTPUrl)
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .addHeader("X-Accept", "application/json")
                .post(requestTokenBody)
                .build();
            return httpClient.newCall(request).execute();
    }
    
    public String loginApprovalUrl(String requestToken) {
        HttpUrl authWithParams = authorizeHTTPUrl.newBuilder()
                .addQueryParameter("request_token", requestToken)
                .addQueryParameter("redirect_uri", redirectUrl)
                .addQueryParameter("mobile", "1")
                .build();
        return authWithParams.toString();
    }
    
    public PocketAccessTokenPayload getAccessTokenPayload() {
        if (key == null || getCode() == null || getCode().isEmpty()) {
            return null;
        }
        PocketAccessTokenPayload accessTokenPayload = new PocketAccessTokenPayload();
        accessTokenPayload.setCode(getCode());
        accessTokenPayload.setConsumerKey(key);
        return accessTokenPayload;
    }

    public Response getAccessToken(PocketAccessTokenPayload payload) {
        try {
            RequestBody r = RequestBody.create(MediaType.parse("application/json"), mapper.writeValueAsString(payload));
            Request request = new Request.Builder()
                .url(AccessTokenUrl)
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .addHeader("X-Accept", "application/json")
                .post(r)
                .build();
            return httpClient.newCall(request).execute();
            
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return null;
    }
    
    public OkHttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        storage.savePocketToken(accessToken);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        storage.savePocketUser(username);
    }
    

}
