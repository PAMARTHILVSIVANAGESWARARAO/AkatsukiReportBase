package com.akatsuki_news_application.AkatsukiReportBase.source.dto;

public class ReviewRequest {

    private String akatsukiMemberName;
    private String newsHeadline;
    private String reviewText;
    private Integer rating;

    public ReviewRequest() {
    }

    public ReviewRequest(String akatsukiMemberName, String newsHeadline, String reviewText, Integer rating) {
        this.akatsukiMemberName = akatsukiMemberName;
        this.newsHeadline = newsHeadline;
        this.reviewText = reviewText;
        this.rating = rating;
    }

    public String getAkatsukiMemberName() {
        return akatsukiMemberName;
    }

    public void setAkatsukiMemberName(String akatsukiMemberName) {
        this.akatsukiMemberName = akatsukiMemberName;
    }

    public String getNewsHeadline() {
        return newsHeadline;
    }

    public void setNewsHeadline(String newsHeadline) {
        this.newsHeadline = newsHeadline;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}

