package me.bayang.reader.mobilizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.concurrent.Task;
import me.bayang.reader.backend.inoreader.ConnectServer;
import me.bayang.reader.controllers.RssController;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Service
public class MercuryMobilizer {
    
    private static Logger LOGGER = LoggerFactory.getLogger(MercuryMobilizer.class);
    
    @Value("${mercury.key}")
    private String appKey;
    
    // https://mercury.postlight.com/parser?url=
    private static final String APP_URL = "https://mercury.postlight.com/parser";
    
    private static final HttpUrl APP_HTTP_URL = HttpUrl.parse(APP_URL);
    
    private OkHttpClient okClient;
    
    private ResourceBundle bundle = ResourceBundle.getBundle("i18n.translations");
    
    @Resource(name="mapper")
    private ObjectMapper mapper;

    @PostConstruct
    public void initClient() {
        okClient = new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .readTimeout(1, TimeUnit.MINUTES)
                .connectTimeout(1, TimeUnit.MINUTES)
                .build();
    }
    
    public MercuryResult getMercuryResult(String url) {
        HttpUrl parameterUrl = APP_HTTP_URL.newBuilder()
                .setQueryParameter("url", url)
                .build();
        Request request = new Request.Builder()
                .url(parameterUrl)
                .addHeader("x-api-key", appKey)
                .addHeader("Content-Type", "application/json")
                .build();
        try {
            Response r = okClient.newCall(request).execute();
            if (r.isSuccessful()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(r.body().source().inputStream(), "utf-8"));
                MercuryResult m = mapper.readValue(reader, MercuryResult.class);
                ConnectServer.closeReader(reader);
                return m;
            } else {
                RssController.snackbarNotify(bundle.getString("connectionFailure"));
                r.close();
                LOGGER.debug("mercury mobilizer error code {} for {}",r.code(), request.url());
            }
        } catch (IOException e) {
            RssController.snackbarNotify(bundle.getString("connectionFailure"));
        }
        return null;
    }
    
    public Task<MercuryResult> getMercuryResultTask(String url) {
        Task<MercuryResult> t = new Task<MercuryResult>() {
            @Override
            protected MercuryResult call() throws Exception {
                LOGGER.debug("starting to fetch mercury for url {}", url);
                return getMercuryResult(url);
            }
        };
        return t;
    }
    
    public static String formatContent(String imgUrl, String pageUrl, String content) {
        StringBuilder sb = new StringBuilder();
        sb.append("<style> p { padding: 5px 50px 5px 50px; } font { font: 400 18px/1.62 Helvetica, Tahoma, Arial, STXihei, \"华文细黑\", \"Microsoft YaHei\", \"微软雅黑\", SimSun, \"宋体\", Heiti, \"黑体\", sans-serif; } img { border-radius: 10px; max-width: 100%; height: auto; } </style> <body><font>");
        sb.append("<div><img src=\"");
        sb.append(imgUrl);
        sb.append("\"></img>");
        sb.append("</div>");
        if (! pageUrl.startsWith("/")) {
            sb.append("<div><a href=\"");
            sb.append(pageUrl);
            sb.append("\">");
            sb.append(pageUrl);
            sb.append("</a>\"");
            sb.append("</div>");
        }
        
        sb.append(content);
        sb.append("</font></body>");
        return sb.toString();
    }
    
}
