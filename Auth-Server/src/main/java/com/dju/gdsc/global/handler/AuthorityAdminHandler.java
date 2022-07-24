package com.dju.gdsc.global.handler;

import com.dju.gdsc.domain.member.model.RoleType;
import com.dju.gdsc.domain.oauth.token.AuthToken;
import com.dju.gdsc.domain.oauth.token.AuthTokenProvider;
import com.dju.gdsc.domain.oauth.utils.HeaderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorityAdminHandler implements HandlerInterceptor {
    private final AuthTokenProvider tokenProvider;


    @Override
    public boolean preHandle(javax.servlet.http.HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String accessToken = HeaderUtil.getAccessToken(request);
        AuthToken authToken = tokenProvider.convertAuthToken(accessToken);
        if(authToken.getTokenClaims().get("role").equals(RoleType.LEAD.getCode()) || authToken.getTokenClaims().get("role").equals(RoleType.CORE.getCode())){
            return true;
        }
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
        return false;
    }
}
