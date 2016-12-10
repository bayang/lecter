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
        sb.append("<style> p { padding: 5px 50px 5px 50px; } font { font: 400 18px/1.62 Helvetica, Tahoma, Arial, STXihei, \"华文细黑\", \"Microsoft YaHei\", \"微软雅黑\", SimSun, \"宋体\", Heiti, \"黑体\", sans-serif; } img { border-radius: 10px; max-width: 100%; height: auto; } </style> <body><font>");
        sb.append("<h1>");
        sb.append(title);
        sb.append("</h1>");
        sb.append(content);
        sb.append("</font></body>");
        return sb.toString();
    }

    public static void main(String[] args) {
        String title = "华为2016年智能手机要卖1.4亿部，高端（价）机出货凶猛";
        String content = "<p>中国的智能手机厂商中，华为已经站稳了全球第三，仅此三星、苹果，而志怀高远的华为手机在余承东的带领下也不甘心只当老三，豪言四五年内就能超越苹果三星。为了实现这个目标，华为今年的智能手机出货目标超过1.4亿部，其中中高端产品营收要占到55%，目前Mate \n \n8手机出货已超过300万部，Mate 7超过700万部，而P8出货超过500万部。</p><p style=\"text-align:center;\"><img src=\"http://img.expreview.com/news/2016/03/15/hw_mes.jpg\" alt=\"hw_mes.jpg\"><br>华为今年在营销上投入了很大精力，梅球王也跟着爵士人生了</p><p>微博ID为<a href=\"http://weibo.com/isuppli?from=feed&amp;loc=at&amp;nick=Kevin%E7%8E%8B%E7%9A%84%E6%97%A5%E8%AE%B0%E6%9C%AC&amp;is_all=1#_rnd1460445156034\">Kevin王的日记本</a>（iHS中国区总监王阳）今天曝光了几张华为智能手机业务的PPT，看样子应该是某些内部会议上透露的数据，其中2016年华为智能手机出货量目标是超过1.4亿部，而2015年华为智能手机出货量为1.08亿部，之前透露的2016年出货量目标也不过1.2亿部，因为公认2016年智能手机市场也会遭遇红海，不过从现在的数据来看，华为智能手机今年还会突飞猛进，差不多也要增长30%，这速度够惊人的。</p><p style=\"text-align:center;\"><img src=\"http://img.expreview.com/news/2016/04/12/hw_4.jpg\" width=\"700\" height=\"501\" alt=\"hw_4.jpg\"><br>华为智能手机出货量目标超过1.4亿部</p><p>与出货量目标相比，华为更大的转变其实还是跳出低价，预计中端到高端手机所占的比例要达到55%以上，而海外市场的营收要占到40%以上。</p><p style=\"text-align:center;\"><img src=\"http://img.expreview.com/news/2016/04/12/hw_1.jpg\" width=\"700\" height=\"535\" alt=\"hw_1.jpg\"><br>华为高端手机销量</p><p>2015华为中高端手机销量占比约为30%，同比增长了150%。截至今年3月31日，华为Mate 8手机销量超过300万，P8手机超过500万，Mate \n \n7手机销量超过了700万部，考虑到这些手机售价多在3000+以上，华为的高端（价）机营收不比国内的O、V两家差。</p><p style=\"text-align:center;\"><img src=\"http://img.expreview.com/news/2016/04/12/hw_2.jpg\" width=\"700\" height=\"536\" alt=\"hw_2.jpg\"><br>球王友情出境（差点以为是张学友）</p><p>这张图诠释了华为品牌内涵——高端、时尚、创新以及标志性技术品牌。不过大家对华为的品牌内涵认可多少可就不一样了——小编认为华为因为SoC处理器的关系是具备相当技术实力的，但真心没觉得华为手机高端或者时尚在哪里，大黑边就不能改改吗？（HW海军求放过）</p><p style=\"text-align:center;\"><img src=\"http://img.expreview.com/news/2016/04/12/hw_3.jpg\" width=\"691\" height=\"541\" alt=\"hw_3.jpg\"><br>华为的产品策略</p><p>最后就是华为不同手机产品的市场策略了，Mate及P系列毫无疑问是针对高端市场的，价格在399美元以上，199-399美元之间的是G+、荣耀等品牌，199美元以内的则是G、Y及部分荣耀系列了。</p>";
        System.out.println(processContent(title, content));
    }
}