package com.dju.gdsc.domain.oauth.service;

import com.dju.gdsc.domain.common.dto.Response;
import com.dju.gdsc.domain.common.properties.AppProperties;
import com.dju.gdsc.domain.member.entity.Member;
import com.dju.gdsc.domain.member.entity.MemberInfo;
import com.dju.gdsc.domain.member.model.RoleType;
import com.dju.gdsc.domain.member.repository.MemberRepository;
import com.dju.gdsc.domain.member.service.MemberService;
import com.dju.gdsc.domain.oauth.dto.TokenResponseDto;
import com.dju.gdsc.domain.oauth.entity.ProviderType;
import com.dju.gdsc.domain.oauth.entity.UserRefreshToken;
import com.dju.gdsc.domain.oauth.handler.Oauth2Handler;
import com.dju.gdsc.domain.oauth.info.OAuth2UserInfo;
import com.dju.gdsc.domain.oauth.info.OAuth2UserInfoFactory;
import com.dju.gdsc.domain.oauth.infrastructure.GoogleOAuthRequester;
import com.dju.gdsc.domain.oauth.repository.UserRefreshTokenRepository;
import com.dju.gdsc.domain.oauth.token.AuthToken;
import com.dju.gdsc.domain.oauth.token.AuthTokenProvider;
import com.nimbusds.oauth2.sdk.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class Oauth2NotWebService {
    private final MemberRepository memberRepository;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final Oauth2Handler oauth2Handler;

    private final AppProperties appProperties;
    private final AuthTokenProvider tokenProvider;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    @Transactional
    public TokenResponseDto signInByOAuth(final ProviderType provider, final String code) {
        OAuth2UserInfo userInfo = oauth2Handler.getUserInfo(provider, code);
        Member member = memberRepository.findByProviderTypeAndUserId(provider, userInfo.getId())
                .orElseGet(() -> customOAuth2UserService.createUser(userInfo , provider));
        return createNewTokens(member);
    }

    private TokenResponseDto createNewTokens(final Member member) {
        Date now = new Date();
        Date authTokenExpiration = new Date(now.getTime() + appProperties.getAuth().getTokenExpiry());
        AuthToken authToken = tokenProvider.createAuthToken
                (
                        member.getUserId() ,
                        member.getRole().getCode() ,
                        authTokenExpiration
                );
        AuthToken refreshToken = tokenProvider.createAuthToken
                (
                        member.getUserId() ,
                        member.getRole().getCode() ,
                        new Date(now.getTime() + appProperties.getAuth().getRefreshTokenExpiry())
                );
        UserRefreshToken userRefreshToken = new UserRefreshToken( member.getUserId() , refreshToken.getToken() );
        userRefreshTokenRepository.save(userRefreshToken);
        return TokenResponseDto.builder()
                .accessToken(authToken.getToken())
                .refreshToken(refreshToken.getToken())
                .expireIn(authTokenExpiration.getTime())
                .build();
    }

}
