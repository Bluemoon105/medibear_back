package com.app.medibear.mapper;

import com.app.medibear.model.SleepData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Mapper
public interface SleepMapper {
    Optional<SleepData> findById(@Param("id") Long id);
    void insert(SleepData data);
    void updateFatigue(SleepData data);
    void updateOptimal(SleepData data);
    
    List<SleepData> getRecentSleepHours(@Param("userId") Long userId);
    
    boolean existsTodayRecord(@Param("userId") Long userId, @Param("today") LocalDate today);
}