package com.app.medibear.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return factory -> factory.addConnectorCustomizers(connector -> {
            // 이미 톰캣에서 설정함 (중복)
//            connector.setMaxPostSize(100 * 1024 * 1024);     // 100MB
            connector.setMaxSavePostSize(100 * 1024 * 1024); // 100MB

            // 이미 톰캣에서 설정함 (중복)
//            connector.setProperty("maxSwallowSize", "-1");   // 제한 해제
//            connector.setProperty("maxHttpHeaderSize", String.valueOf(32 * 1024)); // 헤더 크기 확장
        });
    }
}

