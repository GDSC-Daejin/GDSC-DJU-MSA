package com.dju.gdsc.domain.common.config.corsConfig;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    private static final long MAX_AGE_SECS = 3600L;
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*") // 모든 Origin 추가
                .allowCredentials(true) // 내 서버가 응답할때 json을 자바스크립트가 처리할수있게 할건지
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // 사용하지 않는 METHOD (PATCH) 추가
                .allowedHeaders("*") // 모든 헤더 추가
                .maxAge(MAX_AGE_SECS);
    }
}
