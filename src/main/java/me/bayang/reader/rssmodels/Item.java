package me.bayang.reader.rssmodels;
import java.util.ArrayList;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

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
    private BooleanProperty readPropperty = new SimpleBooleanProperty(isRead);
    
    public Item() {
    }

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
        return readProperty().get();
    }

    public void setRead(boolean read) {
        readProperty().set(read);
    }

    public String getDecimalId() {
        return String.valueOf(Long.parseLong(id.substring(id.lastIndexOf("/") + 1), 16));
    }

    public void setCrawlTimeMsec(String crawlTimeMsec) {
        this.crawlTimeMsec = crawlTimeMsec;
    }

    public void setTimestampUsec(String timestampUsec) {
        this.timestampUsec = timestampUsec;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPublished(long published) {
        this.published = published;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public void setCanonical(ArrayList<Canonical> canonical) {
        this.canonical = canonical;
    }

    public void setAlternate(ArrayList<Alternate> alternate) {
        this.alternate = alternate;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setLikingUsers(ArrayList likingUsers) {
        this.likingUsers = likingUsers;
    }

    public void setComments(ArrayList comments) {
        this.comments = comments;
    }

    public void setCommentsNum(int commentsNum) {
        this.commentsNum = commentsNum;
    }

    public void setAnnotations(ArrayList annotations) {
        this.annotations = annotations;
    }

    public void setOrigin(Origin origin) {
        this.origin = origin;
    }
    
    public BooleanProperty readProperty() {
        return readPropperty;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Item [crawlTimeMsec=").append(crawlTimeMsec)
                .append(", timestampUsec=").append(timestampUsec)
                .append(", id=").append(id).append(", categories=")
                .append(categories).append(", title=").append(title)
                .append(", published=").append(published).append(", updated=")
                .append(updated).append(", canonical=").append(canonical)
                .append(", alternate=").append(alternate).append(", summary=")
                .append(summary).append(", author=").append(author)
                .append(", likingUsers=").append(likingUsers)
                .append(", comments=").append(comments).append(", commentsNum=")
                .append(commentsNum).append(", annotations=")
                .append(annotations).append(", origin=").append(origin)
                .append(", isRead=").append(isRead).append("]");
        return builder.toString();
    }

    /**
     * Process the content to show in the RSS and Readability view, such as combine title and content, add background color
     * @param title
     * @param content
     * @return
     */
    public static String processContent(String title, String content) {
        StringBuilder sb = new StringBuilder();
        sb.append("<style> p { padding: 5px 50px 5px 50px; } font { font: 400 18px/1.62 Helvetica, Tahoma, Arial, STXihei, \"华文细黑\", \"Microsoft YaHei\", \"微软雅黑\", SimSun, \"宋体\", Heiti, \"黑体\", sans-serif; } img { border-radius: 10px; max-width: 100%; height: auto; } </style> <body><font>");
        sb.append("<h1>");
        sb.append(title);
        sb.append("</h1>");
        sb.append(content);
        sb.append("</font></body>");
        return sb.toString();
    }

}