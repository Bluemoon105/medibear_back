package com.app.medibear.mapper;

import com.app.medibear.model.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User findById(String id);
}