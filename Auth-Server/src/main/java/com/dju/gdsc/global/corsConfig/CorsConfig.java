package com.dju.gdsc.global.corsConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CorsFilter;
@Configuration
public interface CorsConfig {
    @Bean
    CorsFilter corsFilter();
}
