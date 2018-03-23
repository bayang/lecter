package me.bayang.reader.mobilizer;

import com.fasterxml.jackson.annotation.JsonAlias;

public class MercuryResult {
    
    private String title;
    
    private String content;
    
    @JsonAlias({"date_published"})
    private String datePublished;
    
    @JsonAlias({"lead_image_url"})
    private String imageUrl;
    
    private String dek;
    
    private String url;
    
    private String domain;
    
    private String excerpt;
    
    @JsonAlias({"word_count"})
    private int wordCount;
    
    private String direction;
    
    @JsonAlias({"total_pages"})
    private int totalPages;
    
    @JsonAlias({"rendered_pages"})
    private int renderedPages;
    
    @JsonAlias({"next_page_url"})
    private String nextPageUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDatePublished() {
        return datePublished;
    }

    public void setDatePublished(String datePublished) {
        this.datePublished = datePublished;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDek() {
        return dek;
    }

    public void setDek(String dek) {
        this.dek = dek;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getRenderedPages() {
        return renderedPages;
    }

    public void setRenderedPages(int renderedPages) {
        this.renderedPages = renderedPages;
    }

    public String getNextPageUrl() {
        return nextPageUrl;
    }

    public void setNextPageUrl(String nextPageUrl) {
        this.nextPageUrl = nextPageUrl;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MercuryResult [title=").append(title)
                .append(", content=").append(content).append(", datePublished=")
                .append(datePublished).append(", imageUrl=").append(imageUrl)
                .append(", dek=").append(dek).append(", url=").append(url)
                .append(", domain=").append(domain).append(", excerpt=")
                .append(excerpt).append(", wordCount=").append(wordCount)
                .append(", direction=").append(direction)
                .append(", totalPages=").append(totalPages)
                .append(", renderedPages=").append(renderedPages)
                .append(", nextPageUrl=").append(nextPageUrl).append("]");
        return builder.toString();
    }
    

}
