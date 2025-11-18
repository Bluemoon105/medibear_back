package com.app.medibear.config.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        // JwtAuthorizationFilter에서 넣어둔 예외 코드 가져오기
        String exception = (String) request.getAttribute("exception");

        String code;
        String message;

        if (exception == null) {
            code = "UNAUTHORIZED";
            message = "권한이 없습니다.";
        } else if (exception.equals("EXPIRED_ACCESS_TOKEN")) {
            code = "EXPIRED_ACCESS_TOKEN";
            message = "AccessToken이 만료되었습니다.";
        } else if (exception.equals("INVALID_TOKEN")) {
            code = "INVALID_TOKEN";
            message = "유효하지 않은 토큰입니다.";
        } else {
            code = "UNAUTHORIZED";
            message = "인증이 필요합니다.";
        }

        Map<String, String> result = new HashMap<>();
        result.put("code", code);
        result.put("message", message);

        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
