package com.app.medibear.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    // FastAPI와의 HTTP 통신을 위한 RestTemplate Bean 등록 (동기 방식)
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
