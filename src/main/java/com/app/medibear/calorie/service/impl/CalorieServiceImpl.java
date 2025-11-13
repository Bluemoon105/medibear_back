package com.app.medibear.calorie.service.impl;

import com.app.medibear.calorie.dao.CalorieDao;
import com.app.medibear.calorie.service.face.CalorieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalorieServiceImpl implements CalorieService {
    private final CalorieDao calorieDao;

    @Override
    public void addTest() {
        calorieDao.insertTest();
    }
}
