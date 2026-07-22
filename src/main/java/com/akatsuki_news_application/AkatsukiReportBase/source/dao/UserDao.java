package com.akatsuki_news_application.AkatsukiReportBase.source.dao;

import java.util.Optional;

import com.akatsuki_news_application.AkatsukiReportBase.source.model.User;

public interface UserDao {

    User save(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}

