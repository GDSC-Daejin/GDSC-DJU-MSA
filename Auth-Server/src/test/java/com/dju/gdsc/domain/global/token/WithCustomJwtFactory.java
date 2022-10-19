package com.dju.gdsc.domain.global.token;

import com.dju.gdsc.domain.common.provider.ApplicationContextProvider;
import com.dju.gdsc.domain.oauth.token.AuthToken;
import com.dju.gdsc.domain.oauth.token.AuthTokenProvider;
import okhttp3.HttpUrl;

import javax.servlet.http.Cookie;
import java.util.Date;

public class WithCustomJwtFactory {
    public static String create(String userId , String role , long expiredTime) {
        AuthTokenProvider authTokenProvider = ApplicationContextProvider.getApplicationContext()
                .getBean(AuthTokenProvider.class);
        Date now = new Date();
        AuthToken authToken = authTokenProvider.createAuthToken(userId, role, new Date(now.getTime() + expiredTime));
        return authToken.getToken();
    }
    public static AuthToken createToken(String userId , String role , long expiredTime) {
        AuthTokenProvider authTokenProvider = ApplicationContextProvider.getApplicationContext()
                .getBean(AuthTokenProvider.class);
        Date now = new Date();
        return authTokenProvider.createAuthToken(userId, role, new Date(now.getTime() + expiredTime));
    }
    public static Cookie getRefreshTokenCookies(String token) {
        AuthTokenProvider authTokenProvider = ApplicationContextProvider.getApplicationContext()
                .getBean(AuthTokenProvider.class);
        // 현재 포트 번호 가져오기
        AuthToken refreshToken = authTokenProvider.convertAuthToken(token);
        return new Cookie("refresh_token", refreshToken.getToken());
    }
}
