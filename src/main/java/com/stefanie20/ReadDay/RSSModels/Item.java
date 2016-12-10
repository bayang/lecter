package com.stefanie20.ReadDay.RSSModels;
import java.util.ArrayList;

public class Item{
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