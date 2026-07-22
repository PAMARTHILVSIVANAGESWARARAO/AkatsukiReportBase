package com.akatsuki_news_application.AkatsukiReportBase.source.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.akatsuki_news_application.AkatsukiReportBase.source.model.Review;
import com.akatsuki_news_application.AkatsukiReportBase.source.model.User;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r JOIN FETCH r.user JOIN FETCH r.akatsukiMember ORDER BY r.createdAt DESC")
    List<Review> findAllWithUserAndMember();

    List<Review> findByUser(User user);

    Optional<Review> findByIdAndUser(Long id, User user);
}

