package com.app.medibear.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeRequest {
	private String email;
	private String oldPassword;
	private String newPassword;
}
