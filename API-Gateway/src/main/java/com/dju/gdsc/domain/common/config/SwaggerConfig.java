package com.dju.gdsc.domain.common.config;

import org.springdoc.core.SwaggerUiConfigParameters;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public CommandLineRunner openApiGroups(
            RouteDefinitionLocator locator,
            SwaggerUiConfigParameters swaggerUiParameters) {
        return args -> locator
                .getRouteDefinitions().collectList().block()
                .stream()
                .map(RouteDefinition::getId)
                .filter(id -> id.matches(".*-route"))
                .forEach(swaggerUiParameters::addGroup);
    }
}