package com.app.medibear.model;

import java.time.LocalDate;

public class User {
    private String id;         //문자열 기반 ID
    private String name;
    private String gender;
    private LocalDate birthDate;

    // getter/setter 정상화
    public String getId() {
        return id;
    }

    public void setId(String id) {  //String 타입으로 수정
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
}
