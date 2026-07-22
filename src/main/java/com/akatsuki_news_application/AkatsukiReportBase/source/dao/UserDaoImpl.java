package com.akatsuki_news_application.AkatsukiReportBase.source.dao;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.akatsuki_news_application.AkatsukiReportBase.source.model.User;
import com.akatsuki_news_application.AkatsukiReportBase.source.repository.UserRepository;

@Repository
public class UserDaoImpl implements UserDao {

    private final UserRepository userRepository;

    public UserDaoImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}

