package com.dju.gdsc.domain.global.token;

import com.dju.gdsc.domain.common.provider.ApplicationContextProvider;
import com.dju.gdsc.domain.oauth.token.AuthToken;
import com.dju.gdsc.domain.oauth.token.AuthTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;

public class WithCustomJwtFactory {
    public static String create(String userId , String role , long expiredTime) {
        AuthTokenProvider authTokenProvider = ApplicationContextProvider.getApplicationContext()
                .getBean(AuthTokenProvider.class);
        Date now = new Date();
        AuthToken authToken = authTokenProvider.createAuthToken(userId, role, new Date(now.getTime() + expiredTime));
        return authToken.getToken();
    }
}
