package com.akatsuki_news_application.AkatsukiReportBase.source.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "akatsuki_members")
public class AkatsukiMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "news_type", nullable = false)
    private String newsType;

    @Column(name = "news_scraped_url", nullable = false, length = 2048)
    private String newsScrapedUrl;

    public AkatsukiMember() {
    }

    public AkatsukiMember(String name, String newsType, String newsScrapedUrl) {
        this.name = name;
        this.newsType = newsType;
        this.newsScrapedUrl = newsScrapedUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNewsType() {
        return newsType;
    }

    public void setNewsType(String newsType) {
        this.newsType = newsType;
    }

    public String getNewsScrapedUrl() {
        return newsScrapedUrl;
    }

    public void setNewsScrapedUrl(String newsScrapedUrl) {
        this.newsScrapedUrl = newsScrapedUrl;
    }
}

