package com.app.medibear.controller;

import com.app.medibear.dto.SignUpRequest;
import com.app.medibear.model.User;
import com.app.medibear.service.UserService;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {
	
	private final UserService userService;
	
	public AuthController(UserService userService) {
		this.userService = userService;
	}
	
	@PostMapping("/signUp")
	public ResponseEntity<?> signup(@RequestBody SignUpRequest req) {
		try {
			User saved = userService.createUser(req);
			return ResponseEntity.ok(saved);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
		}
	}
}