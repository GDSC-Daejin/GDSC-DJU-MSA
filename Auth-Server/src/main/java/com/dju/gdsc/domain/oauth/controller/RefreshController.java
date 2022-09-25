package com.dju.gdsc.domain.oauth.controller;


import com.dju.gdsc.domain.member.model.RoleType;
import com.dju.gdsc.domain.oauth.entity.UserRefreshToken;
import com.dju.gdsc.domain.oauth.repository.UserRefreshTokenRepository;
import com.dju.gdsc.domain.oauth.token.AuthToken;
import com.dju.gdsc.domain.oauth.token.AuthTokenProvider;
import com.dju.gdsc.domain.oauth.utils.CookieUtil;
import com.dju.gdsc.domain.oauth.utils.HeaderUtil;
import com.dju.gdsc.domain.common.dto.Response;
import com.dju.gdsc.domain.common.properties.AppProperties;
import com.dju.gdsc.domain.oauth.utils.JwtCookieUtil;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

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
    private final static String Authorization = "token";
    private String checkToken(HttpServletRequest request) {
        String token = HeaderUtil.getAccessToken(request);
        if (token == null) {
            token = CookieUtil.getCookie(request, Authorization).isPresent() ? CookieUtil.getCookie(request, Authorization).get().getValue() : null;
        }
        return token;
    }
    private String checkRefreshToken(HttpServletRequest request) {
        String token = HeaderUtil.getHeaderRefreshToken(request);
        if (token == null) {
            token = CookieUtil.getCookie(request, REFRESH_TOKEN).isPresent() ? CookieUtil.getCookie(request, REFRESH_TOKEN).get().getValue() : null;
            log.info("refresh token from cookie := [{}]", token);
        }
        return token;
    }

    @GetMapping("/refresh")
    @Operation(summary = "refresh 토큰을 이용하여 JWT 토큰 재발급", description = "토큰이 expired 되어야 작동함")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "재발급 성공"),
            @ApiResponse(responseCode = "401", description = "재발급 실패")
    })
    public Response refreshToken (HttpServletRequest request, HttpServletResponse response) {
        // access token 확인
        AuthToken authToken = tokenProvider.convertAuthToken(checkToken(request));
        if (!authToken.validateWithOutExpired()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return Response.invalidAccessToken();
        }
        // refresh token 확인
        // expired access token 인지 확인
        Claims claims = authToken.getExpiredTokenClaims();
        if (claims == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return Response.notExpiredTokenYet();
        }

        String userId = claims.getSubject();
        RoleType roleType = RoleType.of(claims.get("role", String.class));

        // refresh token
        //String refreshToken = HeaderUtil.getHeaderRefreshToken(request);
        String refreshToken = checkRefreshToken(request);
        AuthToken authRefreshToken = tokenProvider.convertAuthToken(refreshToken);
        log.info("refreshToken: {}", refreshToken);

        if (!authRefreshToken.validate()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
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
        long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();
        // refresh 토큰 기간이 3일 이하로 남은 경우, refresh 토큰 갱신
        if (validTime <= THREE_DAYS_MSEC) {
            // refresh 토큰 설정
            authRefreshToken = tokenProvider.createAuthToken(
                    appProperties.getAuth().getTokenSecret(),new Date(now.getTime() + refreshTokenExpiry)
            );
            long refreshmentExpiry = appProperties.getAuth().getRefreshTokenExpiry();
            int refreshCookieExpiry = (int) (refreshmentExpiry/1000);
            // DB에 refresh 토큰 업데이트
            userRefreshToken.setRefreshToken(authRefreshToken.getToken());
            userRefreshTokenRepository.save(userRefreshToken);
            CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
            CookieUtil.addCookie(request,response, REFRESH_TOKEN, authRefreshToken.getToken(), refreshCookieExpiry);
        }
        long tokenExpiry =appProperties.getAuth().getRefreshTokenExpiry();
        long accessTokenExpiry = appProperties.getAuth().getTokenExpiry();
        int cookieExpiry = (int) (tokenExpiry/1000); // 초 단위로 변경
        JwtCookieUtil.authCookieGenerate(request, response, newAccessToken, cookieExpiry);
        Map<String,String>  tokenMap = new HashMap<>();
        tokenMap.put("token", newAccessToken.getToken());
        return Response.success("data", tokenMap );
    }
}
