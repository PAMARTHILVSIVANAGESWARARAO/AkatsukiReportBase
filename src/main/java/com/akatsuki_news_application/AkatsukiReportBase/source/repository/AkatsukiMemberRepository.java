package com.akatsuki_news_application.AkatsukiReportBase.source.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.akatsuki_news_application.AkatsukiReportBase.source.model.AkatsukiMember;

@Repository
public interface AkatsukiMemberRepository extends JpaRepository<AkatsukiMember, Long> {

    Optional<AkatsukiMember> findByName(String name);

    boolean existsByName(String name);

    @Query("SELECT m.name FROM AkatsukiMember m")
    List<String> findAllNames();
}

