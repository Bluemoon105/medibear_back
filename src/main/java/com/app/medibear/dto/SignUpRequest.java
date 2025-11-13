package com.app.medibear.dto;

import com.app.medibear.model.User.Gender;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {
	
	private String email;
	private String password;
	private Gender gender;
	private String name;
	private String birthDate;
}