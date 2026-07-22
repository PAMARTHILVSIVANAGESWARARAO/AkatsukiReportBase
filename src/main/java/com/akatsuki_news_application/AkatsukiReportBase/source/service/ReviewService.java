package com.akatsuki_news_application.AkatsukiReportBase.source.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.akatsuki_news_application.AkatsukiReportBase.source.dto.ReviewRequest;
import com.akatsuki_news_application.AkatsukiReportBase.source.dto.ReviewResponse;
import com.akatsuki_news_application.AkatsukiReportBase.source.model.AkatsukiMember;
import com.akatsuki_news_application.AkatsukiReportBase.source.model.Review;
import com.akatsuki_news_application.AkatsukiReportBase.source.model.User;
import com.akatsuki_news_application.AkatsukiReportBase.source.repository.AkatsukiMemberRepository;
import com.akatsuki_news_application.AkatsukiReportBase.source.repository.ReviewRepository;
import com.akatsuki_news_application.AkatsukiReportBase.source.repository.UserRepository;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final AkatsukiMemberRepository akatsukiMemberRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         UserRepository userRepository,
                         AkatsukiMemberRepository akatsukiMemberRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.akatsukiMemberRepository = akatsukiMemberRepository;
    }

    @Transactional
    public ReviewResponse createReview(String username, ReviewRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        AkatsukiMember member = akatsukiMemberRepository.findByName(request.getAkatsukiMemberName())
                .orElseThrow(() -> new RuntimeException("Akatsuki member not found: " + request.getAkatsukiMemberName()));

        if (request.getNewsHeadline() == null || request.getNewsHeadline().isBlank()) {
            throw new RuntimeException("newsHeadline is required");
        }
        if (request.getReviewText() == null || request.getReviewText().isBlank()) {
            throw new RuntimeException("reviewText is required");
        }
        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new RuntimeException("rating must be between 1 and 5");
        }

        Review review = new Review(user, member, request.getNewsHeadline(), request.getReviewText(), request.getRating());
        Review savedReview = reviewRepository.save(review);

        return ReviewResponse.fromEntity(savedReview);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getAllReviews() {
        List<Review> reviews = reviewRepository.findAllWithUserAndMember();
        return reviews.stream()
                .map(ReviewResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewResponse updateReview(String username, Long reviewId, ReviewRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Review review = reviewRepository.findByIdAndUser(reviewId, user)
                .orElseThrow(() -> new RuntimeException("Review not found or not authorized to update"));

        if (request.getAkatsukiMemberName() != null && !request.getAkatsukiMemberName().isBlank()) {
            AkatsukiMember member = akatsukiMemberRepository.findByName(request.getAkatsukiMemberName())
                    .orElseThrow(() -> new RuntimeException("Akatsuki member not found: " + request.getAkatsukiMemberName()));
            review.setAkatsukiMember(member);
        }
        if (request.getNewsHeadline() != null && !request.getNewsHeadline().isBlank()) {
            review.setNewsHeadline(request.getNewsHeadline());
        }
        if (request.getReviewText() != null && !request.getReviewText().isBlank()) {
            review.setReviewText(request.getReviewText());
        }
        if (request.getRating() != null) {
            if (request.getRating() < 1 || request.getRating() > 5) {
                throw new RuntimeException("rating must be between 1 and 5");
            }
            review.setRating(request.getRating());
        }

        Review updatedReview = reviewRepository.save(review);
        return ReviewResponse.fromEntity(updatedReview);
    }

    @Transactional
    public void deleteReview(String username, Long reviewId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Review review = reviewRepository.findByIdAndUser(reviewId, user)
                .orElseThrow(() -> new RuntimeException("Review not found or not authorized to delete"));

        reviewRepository.delete(review);
    }
}

