package com.dju.gdsc.domain.oauth.controller;


import com.dju.gdsc.domain.member.model.RoleType;
import com.dju.gdsc.domain.oauth.entity.UserRefreshToken;
import com.dju.gdsc.domain.oauth.repository.UserRefreshTokenRepository;
import com.dju.gdsc.domain.oauth.token.AuthToken;
import com.dju.gdsc.domain.oauth.token.AuthTokenProvider;
import com.dju.gdsc.domain.oauth.utils.HeaderUtil;
import com.dju.gdsc.domain.common.dto.Response;
import com.dju.gdsc.domain.common.properties.AppProperties;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class RefreshController {
    private final AppProperties appProperties;
    private final AuthTokenProvider tokenProvider;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final static long THREE_DAYS_MSEC = 259200000;
    private final static String REFRESH_TOKEN = "refresh_token";
    @GetMapping("/refresh")
    @ApiOperation(value = "refresh 토큰을 이용하여 JWT 토큰 재발급", notes = "토큰이 expired 되어야 작동함")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "JWT 토큰 Bearer 값 필수 ", required = true, paramType = "header", dataType = "string", defaultValue = "Bearer "),
            @ApiImplicitParam(name = "RefreshToken", value = "refresh 토큰 Bearer 값 필수", required = true, paramType = "header", dataType = "string" ,defaultValue = "Bearer ")
    })
    public Response refreshToken (HttpServletRequest request, HttpServletResponse response) {
        // access token 확인
        String accessToken = HeaderUtil.getAccessToken(request);
        AuthToken authToken = tokenProvider.convertAuthToken(accessToken);
        if (!authToken.validateWithOutExpired()) {
            return Response.invalidAccessToken();
        }

        // expired access token 인지 확인
        Claims claims = authToken.getExpiredTokenClaims();
        if (claims == null) {
            return Response.notExpiredTokenYet();
        }

        String userId = claims.getSubject();
        RoleType roleType = RoleType.of(claims.get("role", String.class));

        // refresh token
        String refreshToken = HeaderUtil.getHeaderRefreshToken(request);
        AuthToken authRefreshToken = tokenProvider.convertAuthToken(refreshToken);
        log.info("refreshToken: {}", refreshToken);

        if (!authRefreshToken.validate()) {
            return Response.invalidRefreshToken();
        }

        // userId refresh token 으로 DB 확인
        UserRefreshToken userRefreshToken = userRefreshTokenRepository.findByUserIdAndRefreshToken(userId, refreshToken);
        if (userRefreshToken == null) {
            return Response.invalidRefreshToken();
        }

        Date now = new Date();
        AuthToken newAccessToken = tokenProvider.createAuthToken(
                userId,
                roleType.getCode(),
                new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
        );

        long validTime = authRefreshToken.getTokenClaims().getExpiration().getTime() - now.getTime();

        // refresh 토큰 기간이 3일 이하로 남은 경우, refresh 토큰 갱신
        if (validTime <= THREE_DAYS_MSEC) {
            // refresh 토큰 설정
            long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();

            authRefreshToken = tokenProvider.createAuthToken(
                    appProperties.getAuth().getTokenSecret(),
                    new Date(now.getTime() + refreshTokenExpiry)
            );

            // DB에 refresh 토큰 업데이트
            userRefreshToken.setRefreshToken(authRefreshToken.getToken());
            userRefreshTokenRepository.save(userRefreshToken);
        }
        Authentication authentication = tokenProvider.getAuthentication(newAccessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Map<String,String>  tokenMap = new HashMap<>();
        tokenMap.put("token", newAccessToken.getToken());
        return Response.success("data", tokenMap );
    }
}
