package com.app.medibear.repository;

import com.app.medibear.entity.FitnessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FitnessLogRepository extends JpaRepository<FitnessLog, Long> {
    @Query("""
        SELECT f
        FROM FitnessLog f
        WHERE f.member.memberNo = :memberNo
        AND f.createdAt >= :weekAgo
        ORDER BY f.createdAt DESC
    """)
    List<FitnessLog> findRecentFitnessLogs(Long memberNo, LocalDateTime weekAgo);

    FitnessLog findTopByMember_MemberNoOrderByCreatedAtDesc(Long memberNo);
}
