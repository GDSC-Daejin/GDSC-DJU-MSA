package com.dju.gdsc.domain.oauth.controller;


import com.dju.gdsc.domain.member.entity.Member;
import com.dju.gdsc.domain.member.service.MemberService;
import com.dju.gdsc.domain.oauth.dto.AuthReqModel;

import com.dju.gdsc.domain.oauth.entity.UserPrincipal;
import com.dju.gdsc.domain.oauth.entity.UserRefreshToken;
import com.dju.gdsc.domain.oauth.repository.UserRefreshTokenRepository;
import com.dju.gdsc.domain.oauth.token.AuthToken;
import com.dju.gdsc.domain.oauth.token.AuthTokenProvider;
import com.dju.gdsc.domain.oauth.utils.CookieUtil;
import com.dju.gdsc.domain.common.dto.Response;
import com.dju.gdsc.domain.common.dto.ResponseDto;
import com.dju.gdsc.domain.common.properties.AppProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "사용자 인증 api controller", description = "사용자 인증 api")
public class AuthController {

    private final AppProperties appProperties;
    private final AuthTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private  final MemberService memberService;
    private final static long THREE_DAYS_MSEC = 259200000;
    private final static String REFRESH_TOKEN = "refresh_token";
    @Profile("!real")
    @Operation(summary = "회원가입 테스트용", description = "회원가입 할때 쓰는 놈 Api 테스트 용으로 삭제 예정")
    @PostMapping("/test/auth/join")
    public ResponseDto<Integer> join(@RequestBody Member member) {

        memberService.회원가입(member);
        // 수정필요
        return new ResponseDto<Integer>(HttpStatus.OK, 1, "성공");
    }

    @Operation(summary = "로그인 테스트용", description = "로그인 할때 쓰는 놈 Api 테스트 용으로 삭제 예정")
    @PostMapping("/test/auth/login")
    @Profile("!real")
    public Response login(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody AuthReqModel authReqModel
    ) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authReqModel.getId(),
                        authReqModel.getPassword()
                )
        );
        //System.out.println(authReqModel.getPassword());
        String userId = authReqModel.getId();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Date now = new Date();
        AuthToken accessToken = tokenProvider.createAuthToken(
                userId,
                ((UserPrincipal) authentication.getPrincipal()).getRoleType().getCode(),
                new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
        );

        long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();
        AuthToken refreshToken = tokenProvider.createAuthToken(
                appProperties.getAuth().getTokenSecret(),
                new Date(now.getTime() + refreshTokenExpiry)
        );

        // userId refresh token 으로 DB 확인
        UserRefreshToken userRefreshToken = userRefreshTokenRepository.findByUserId(userId);
        if (userRefreshToken == null) {
            // 없는 경우 새로 등록
            userRefreshToken = new UserRefreshToken(userId, refreshToken.getToken());
            userRefreshTokenRepository.saveAndFlush(userRefreshToken);
        } else {
            // 여기서 업데이트 쿼리가 안나옴
            // DB에 refresh 토큰 업데이트
            userRefreshToken.setRefreshToken(refreshToken.getToken());
            userRefreshTokenRepository.saveAndFlush(userRefreshToken);
        }

        int cookieMaxAge = (int) refreshTokenExpiry / 60;
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
        CookieUtil.addCookie(response, REFRESH_TOKEN, refreshToken.getToken(), cookieMaxAge);

        return Response.success("token", accessToken.getToken());
    }


}