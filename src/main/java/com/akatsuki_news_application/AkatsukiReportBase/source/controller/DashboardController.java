package com.akatsuki_news_application.AkatsukiReportBase.source.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.akatsuki_news_application.AkatsukiReportBase.source.components.NewsFetchingComponent;
import com.akatsuki_news_application.AkatsukiReportBase.source.config.JwtUtil;
import com.akatsuki_news_application.AkatsukiReportBase.source.dto.NewsRequest;
import com.akatsuki_news_application.AkatsukiReportBase.source.model.AkatsukiMember;
import com.akatsuki_news_application.AkatsukiReportBase.source.repository.AkatsukiMemberRepository;
import com.akatsuki_news_application.AkatsukiReportBase.source.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final JwtUtil jwtUtil;
    private final AkatsukiMemberRepository akatsukiMemberRepository;
    private final UserRepository userRepository;
    private final NewsFetchingComponent newsFetchingComponent;

    public DashboardController(JwtUtil jwtUtil,
                               AkatsukiMemberRepository akatsukiMemberRepository,
                               UserRepository userRepository,
                               NewsFetchingComponent newsFetchingComponent) {
        this.jwtUtil = jwtUtil;
        this.akatsukiMemberRepository = akatsukiMemberRepository;
        this.userRepository = userRepository;
        this.newsFetchingComponent = newsFetchingComponent;
    }

    @GetMapping
    public ResponseEntity<?> dashboard(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        String username = jwtUtil.getUsernameFromToken(token);
        java.util.List<String> akatsukiMembers = akatsukiMemberRepository.findAllNames();

        return ResponseEntity.ok(Map.of(
                "username", username,
                "message", "Welcome to the dashboard, " + username + "!",
                "akatsuki_members", akatsukiMembers
        ));
    }

    @GetMapping("/user-count")
    public ResponseEntity<?> getUserCount(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        long userCount = userRepository.count();
        return ResponseEntity.ok(Map.of("user_count", userCount));
    }

    @PostMapping("/news")
    public ResponseEntity<?> getNews(@RequestBody NewsRequest newsRequest,
                                      HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        String memberName = newsRequest.getAkatsukiMemberName();
        if (memberName == null || memberName.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "akatsukiMemberName is required"));
        }

        AkatsukiMember member = akatsukiMemberRepository.findByName(memberName).orElse(null);
        if (member == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Akatsuki member not found: " + memberName));
        }

        LinkedHashMap<String, Object> scrapedNews = newsFetchingComponent.fetchNews(member.getNewsScrapedUrl());

        Map<String, Object> response = new java.util.LinkedHashMap<>();
        response.put("akatsuki_member_name", memberName);
        response.putAll(scrapedNews);

        return ResponseEntity.ok(response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

