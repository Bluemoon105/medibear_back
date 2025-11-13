package com.app.medibear.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


@Configuration
public class WebClientConfig {

  @Value("${cors.fastapi.url}")
  private String corsFastApiUrl;

  @Bean
  public WebClient webClient() {
    return WebClient.builder()
      .baseUrl(corsFastApiUrl)
      .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
      .build();
  }

}
