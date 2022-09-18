package com.dju.gdsc.domain.oauth.handler;


import com.dju.gdsc.domain.oauth.token.AuthToken;
import com.dju.gdsc.domain.oauth.token.AuthTokenProvider;
import com.dju.gdsc.domain.oauth.utils.HeaderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Component
@RequiredArgsConstructor
@Slf4j
 // 추후 테스트 코드에서 401 에러 구현 필요
public class TokenValidateInterceptor implements HandlerInterceptor {
    private final AuthTokenProvider tokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

            String accessToken = HeaderUtil.getAccessToken(request);
            AuthToken authToken = tokenProvider.convertAuthToken(accessToken);
            if(!authToken.validate()){
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                return false;
            }

            return true;

    }
}
