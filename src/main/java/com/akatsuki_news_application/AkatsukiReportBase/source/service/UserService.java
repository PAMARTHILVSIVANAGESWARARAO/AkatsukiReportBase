package com.akatsuki_news_application.AkatsukiReportBase.source.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.akatsuki_news_application.AkatsukiReportBase.source.config.JwtUtil;
import com.akatsuki_news_application.AkatsukiReportBase.source.dao.UserDao;
import com.akatsuki_news_application.AkatsukiReportBase.source.dto.AuthResponse;
import com.akatsuki_news_application.AkatsukiReportBase.source.dto.LoginRequest;
import com.akatsuki_news_application.AkatsukiReportBase.source.dto.RegisterRequest;
import com.akatsuki_news_application.AkatsukiReportBase.source.model.User;

@Service
public class UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public UserService(UserDao userDao,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest request) {
        // Check if email or username already exists
        if (userDao.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered!");
        }
        if (userDao.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken!");
        }

        // Create new user
        User user = new User(
                request.getEmail(),
                request.getUsername(),
                passwordEncoder.encode(request.getPassword())
        );
        userDao.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail(), user.getUsername());

        return new AuthResponse(token, user.getUsername(), "Registration successful!");
    }

    public AuthResponse login(LoginRequest request) {
        // Authenticate
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Fetch user
        User user = userDao.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail(), user.getUsername());

        return new AuthResponse(token, user.getUsername(), "Login successful!");
    }
}

