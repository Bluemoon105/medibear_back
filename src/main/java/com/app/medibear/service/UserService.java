package com.app.medibear.service;

import com.app.medibear.mapper.UserMapper;
import com.app.medibear.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
public class UserService {

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public User getUserById(String userId) {
        User user = userMapper.findById(userId);
        if (user == null)
            throw new IllegalArgumentException("User not found: " + userId);
        return user;
    }

    public int calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    // "M" → 0, "F" → 1
    public int toGenderInt(String genderChar) {
        return "M".equalsIgnoreCase(genderChar) ? 0 : 1;
    }
}