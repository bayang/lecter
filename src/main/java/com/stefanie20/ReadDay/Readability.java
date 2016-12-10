package com.stefanie20.ReadDay;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * This class is used to get information about Readability view.
 * A Readability object also represent a object containing Readability web content.
 */
public class Readability {
    private String domain;
    private String next_page_id;
    private String url;
    private String short_url;
    private String author;
    private String excerpt;
    private String direction;
    private long word_count;
    private long total_pages;
    private String content;
    private String date_published;
    private String dek;
    private String lead_image_url;
    private String title;
    private long rendered_pages;

    private static String readabilityToken = "b3ad4a0d0dae066420cb9a37004a89851a661baa";


    public Readability(String domain, String next_page_id, String url, String short_url, String author, String excerpt, String direction, long word_count, long total_pages, String content, String date_published, String dek, String lead_image_url, String title, long rendered_pages) {
        this.domain = domain;
        this.next_page_id = next_page_id;
        this.url = url;
        this.short_url = short_url;
        this.author = author;
        this.excerpt = excerpt;
        this.direction = direction;
        this.word_count = word_count;
        this.total_pages = total_pages;
        this.content = content;
        this.date_published = date_published;
        this.dek = dek;
        this.lead_image_url = lead_image_url;
        this.title = title;
        this.rendered_pages = rendered_pages;
    }

    /**
     * get the Readability content
     * @param s the url of the website
     * @return  the content of the web
     */
    public static String getReadabilityContent(String s) {
        String url = "https://www.readability.com/api/content/v1/parser?url=" + s + "&token=" + readabilityToken;
        try {
            URLConnection connection = new URL(url).openConnection();
            connection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            Gson gson = new Gson();
            Readability readability = gson.fromJson(reader, Readability.class);
            return readability.getContent();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getNext_page_id() {
        return next_page_id;
    }

    public void setNext_page_id(String next_page_id) {
        this.next_page_id = next_page_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getShort_url() {
        return short_url;
    }

    public void setShort_url(String short_url) {
        this.short_url = short_url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public long getWord_count() {
        return word_count;
    }

    public void setWord_count(long word_count) {
        this.word_count = word_count;
    }

    public long getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(long total_pages) {
        this.total_pages = total_pages;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate_published() {
        return date_published;
    }

    public void setDate_published(String date_published) {
        this.date_published = date_published;
    }

    public String getDek() {
        return dek;
    }

    public void setDek(String dek) {
        this.dek = dek;
    }

    public String getLead_image_url() {
        return lead_image_url;
    }

    public void setLead_image_url(String lead_image_url) {
        this.lead_image_url = lead_image_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getRendered_pages() {
        return rendered_pages;
    }

    public void setRendered_pages(long rendered_pages) {
        this.rendered_pages = rendered_pages;
    }
}

