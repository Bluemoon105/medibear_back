package com.app.medibear.repository;

import com.app.medibear.model.SleepData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SleepDataRepository extends JpaRepository<SleepData, Long> {

    boolean existsByMemberNoAndDate(Long memberNo, LocalDate date);

    Optional<SleepData> findByMemberNoAndDate(Long memberNo, LocalDate date);

    List<SleepData> findTop7ByMemberNoOrderByDateDesc(Long memberNo);
    
    void deleteByMemberNo (Long memberNo);
}
