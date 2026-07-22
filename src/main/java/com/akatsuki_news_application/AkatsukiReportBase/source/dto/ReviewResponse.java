package com.akatsuki_news_application.AkatsukiReportBase.source.dto;

import java.time.LocalDateTime;

import com.akatsuki_news_application.AkatsukiReportBase.source.model.Review;

public class ReviewResponse {

    private Long id;
    private String username;
    private String akatsukiMemberName;
    private String newsHeadline;
    private String reviewText;
    private Integer rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ReviewResponse() {
    }

    public static ReviewResponse fromEntity(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setUsername(review.getUser().getUsername());
        response.setAkatsukiMemberName(review.getAkatsukiMember().getName());
        response.setNewsHeadline(review.getNewsHeadline());
        response.setReviewText(review.getReviewText());
        response.setRating(review.getRating());
        response.setCreatedAt(review.getCreatedAt());
        response.setUpdatedAt(review.getUpdatedAt());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

