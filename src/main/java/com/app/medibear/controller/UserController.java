package com.app.medibear.controller;

import com.app.medibear.dto.PasswordChangeRequest;
import com.app.medibear.model.User;
import com.app.medibear.service.UserService;
import com.app.medibear.utils.GetMemberId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final GetMemberId getMemberId;

//    public UserController(UserService userService) {
//        this.userService = userService;
//    }

    /** email 기반 회원 조회 */
    @PostMapping("/get")
    public ResponseEntity<?> getUser(@RequestBody Map<String, String> req, HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        String memberId =  getMemberId.getMemberId(authorizationHeader);
        String email = req.get("email");
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    /** 비밀번호 변경 (email 기반) */
    @PatchMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest req, HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        String memberId =  getMemberId.getMemberId(authorizationHeader);
        try {
            userService.changePassword(req);
            return ResponseEntity.ok(Map.of("message", "비밀번호가 성공적으로 변경되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** 회원 탈퇴 (email 기반) */
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestBody Map<String, String> req, HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        String memberId =  getMemberId.getMemberId(authorizationHeader);
        String email = req.get("email");

        userService.deleteUser(email);
        return ResponseEntity.ok(Map.of("message", "회원 탈퇴가 완료되었습니다."));
    }
}
