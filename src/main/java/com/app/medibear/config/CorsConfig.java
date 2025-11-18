package com.app.medibear.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/exercise/**")
                //백엔드 API 접근을 허용할 "출처(origin)" 목록
                .allowedOrigins("http://localhost:5173","http://127.0.0.1:5173")
//                        "http://localhost:3000","http://127.0.0.1:3000")
                .allowedMethods("GET","POST","OPTIONS")
                .allowedHeaders("*")
                //쿠키·세션·인증정보 포함 요청을 허용할지 여부
                .allowCredentials(true)
                //Preflight 요청(OPTIONS) 결과를 브라우저가 캐시할 시간(초)
                .maxAge(86400);
    }
}