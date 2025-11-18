package com.app.medibear.repository;

import com.app.medibear.entity.FitnessReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FitnessReportRepository extends JpaRepository<FitnessReport, Long> {

    @Query("""
        SELECT r FROM FitnessReport r
        WHERE r.member.memberNo = :memberNo
        ORDER BY r.createdAt DESC
        """)
    List<FitnessReport> findLatestReport(Long memberNo);

}
