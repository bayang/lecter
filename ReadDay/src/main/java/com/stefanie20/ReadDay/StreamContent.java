package com.stefanie20.ReadDay;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * A StreamContent represents a object get from Inoreader's <a href="https://www.inoreader.com/developers/stream-contents">Stream Contents</a>
 */
public class StreamContent {
    private String direction;
    private String id;
    private String title;
    private String description;
    private Self self;
    private long updated;
    private String updatedUsec;
    private ArrayList<Item> items;
    private String continuation;

    public StreamContent(String direction, String id, String title, String description, Self self, long updated, String updatedUsec, ArrayList<Item> items, String continuation) {
        this.direction = direction;
        this.id = id;
        this.title = title;
        this.description = description;
        this.self = self;
        this.updated = updated;
        this.updatedUsec = updatedUsec;
        this.items = items;
        this.continuation = continuation;
    }

    public static List<Item> getStreamContent(String URLString) {
        BufferedReader reader = ConnectServer.connectServer(URLString);
        Gson gson = new Gson();
        StreamContent content = gson.fromJson(reader, StreamContent.class);
        List<Item> itemList = new ArrayList<>(content.getItems());

        while (content.getContinuation() != null) {
            reader = ConnectServer.connectServer(URLString + "&c=" + content.getContinuation());
            content = gson.fromJson(reader, StreamContent.class);
            itemList.addAll(content.getItems());
        }
        return itemList;
    }


    public static void main(String[] args) throws Exception{
        List<Item> list = getStreamContent(ConnectServer.starredContentURL);
        PrintWriter pw = new PrintWriter("output.txt");
        for (Item item : list) {
            pw.println(item.getTitle());
            pw.println(item.getCategories());
        }
        pw.close();
    }

    public String getDirection() {
        return direction;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Self getSelf() {
        return self;
    }

    public long getUpdated() {
        return updated;
    }

    public String getUpdatedUsec() {
        return updatedUsec;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public String getContinuation() {
        return continuation;
    }

}

class Self {
    private String href;

    public Self(String href) {
        this.href = href;
    }

    public String getHref() {
        return href;
    }
}

class Item{
    private String crawlTimeMsec;
    private String timestampUsec;
    private String id;
    private ArrayList<String> categories;
    private String title;
    private long published;
    private long updated;
    private ArrayList<Canonical> canonical;
    private ArrayList<Alternate> alternate;
    private Summary summary;
    private String author;
    private ArrayList likingUsers;
    private ArrayList comments;
    private int commentsNum;
    private ArrayList annotations;
    private Origin origin;
    private boolean isRead = false;//doesn't include in the stream, but add this boolean to determine the color in listView

    public Item(String crawlTimeMsec, String timestampUsec, String id, ArrayList<String> categories, String title, long published, long updated, ArrayList<Canonical> canonical, ArrayList<Alternate> alternate, Summary summary, String author, ArrayList likingUsers, ArrayList comments, int commentsNum, ArrayList annotations, Origin origin) {
        this.crawlTimeMsec = crawlTimeMsec;
        this.timestampUsec = timestampUsec;
        this.id = id;
        this.categories = categories;
        this.title = title;
        this.published = published;
        this.updated = updated;
        this.canonical = canonical;
        this.alternate = alternate;
        this.summary = summary;
        this.author = author;
        this.likingUsers = likingUsers;
        this.comments = comments;
        this.commentsNum = commentsNum;
        this.annotations = annotations;
        this.origin = origin;
    }


    public String getCrawlTimeMsec() {
        return crawlTimeMsec;
    }

    public String getTimestampUsec() {
        return timestampUsec;
    }

    public String getId() {
        return id;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public String getTitle() {
        return title;
    }

    public long getPublished() {
        return published;
    }

    public long getUpdated() {
        return updated;
    }

    public ArrayList<Canonical> getCanonical() {
        return canonical;
    }

    public ArrayList<Alternate> getAlternate() {
        return alternate;
    }

    public Summary getSummary() {
        return summary;
    }

    public String getAuthor() {
        return author;
    }

    public ArrayList getLikingUsers() {
        return likingUsers;
    }

    public ArrayList getComments() {
        return comments;
    }

    public int getCommentsNum() {
        return commentsNum;
    }

    public ArrayList getAnnotations() {
        return annotations;
    }

    public Origin getOrigin() {
        return origin;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getDecimalId() {
        return String.valueOf(Long.parseLong(id.substring(id.lastIndexOf("/") + 1), 16));
    }

    /**
     * Process the content to show in the RSS and Readability view, such as combine title and content, add background color
     * @param title
     * @param content
     * @return
     */
    public static String processContent(String title, String content) {
        StringBuilder sb = new StringBuilder();
        sb.append("<style> p { padding: 5px 50px 5px 50px; } font { font: 400 18px/1.62 \"Georgia\", \"Xin Gothic\", \"Hiragino Sans GB\", \"Droid Sans Fallback\", \"Microsoft YaHei\", sans-serif; } img { border-radius: 10px; max-width: 100%; height: auto; } </style> <body><font>");
        sb.append("<h1>");
        sb.append(title);
        sb.append("</h1>");
        sb.append(content);
        sb.append("</font></body>");
        return sb.toString();
    }
}

class Canonical{
    private String href;

    public Canonical(String href) {
        this.href = href;
    }

    public String getHref() {
        return href;
    }
}

class Alternate{
    private String href;
    private String type;

    public Alternate(String href, String type) {
        this.href = href;
        this.type = type;
    }

    public String getHref() {
        return href;
    }

    public String getType() {
        return type;
    }
}

class Summary{
    private String direction;
    private String content;

    public Summary(String direction, String content) {
        this.direction = direction;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String getDirection() {
        return direction;
    }
}

class Origin{
    private String streamId;
    private String title;
    private String htmlUrl;

    public Origin(String streamId, String title, String htmlUrl) {
        this.streamId = streamId;
        this.title = title;
        this.htmlUrl = htmlUrl;
    }

    public String getStreamId() {
        return streamId;
    }

    public String getTitle() {
        return title;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }
}