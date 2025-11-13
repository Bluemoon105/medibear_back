package com.app.medibear.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

  @Value("${cors.fastapi.url}")
  private String corsFastApiUrl;

  @Bean
  public WebClient webClient() {
    return WebClient.builder()
      .baseUrl(corsFastApiUrl)
      .build();
  }

}
