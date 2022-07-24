package com.dju.gdsc.global.security;


import com.dju.gdsc.domain.oauth.handler.TokenValidateInterceptor;
import com.dju.gdsc.global.handler.AuthorityAdminHandler;
import com.dju.gdsc.global.handler.AuthorityNotGuestHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@Profile("!test") // 추후 테스트 코드에서 401 에러 구현 필요
public class WebConfig implements WebMvcConfigurer {

    private final TokenValidateInterceptor tokenValidateInterceptor;
    private final AuthorityAdminHandler authorityAdminHandler;
    private final AuthorityNotGuestHandler authorityNotGuestHandler;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 처음 접속할 때 토큰 검증 인터셉터 추가
        registry.addInterceptor(tokenValidateInterceptor)
                .addPathPatterns("/member-route/**")
                .excludePathPatterns("/refresh");
        // 이후 접속할 때 권한 검증 인터셉터 추가

    }


}
