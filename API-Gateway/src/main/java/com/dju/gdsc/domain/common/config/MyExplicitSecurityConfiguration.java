package com.dju.gdsc.domain.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
public class MyExplicitSecurityConfiguration {
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf().disable()
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/swagger-ui.html").hasAnyRole("ADMIN", "USER")
                        .pathMatchers("/webjars/**").hasAnyRole("ADMIN", "USER")
                        .pathMatchers("/v3/api-docs/**").hasAnyRole("ADMIN", "USER")
                        .pathMatchers("**/api/**").permitAll()
                        .pathMatchers("/test/**").permitAll()
                        .pathMatchers("/refresh").permitAll()
                )
                .httpBasic().and()
                .formLogin();
        return http.build();
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("gdscdju")
                .password(passwordEncoder().encode("jason")).roles("USER").build();
        return new MapReactiveUserDetailsService(user);
    }
}