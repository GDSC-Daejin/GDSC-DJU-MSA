package com.dju.gdsc.domain.oauth.service;

import com.dju.gdsc.domain.common.properties.AppProperties;
import com.dju.gdsc.domain.member.entity.Member;
import com.dju.gdsc.domain.member.repository.MemberRepository;
import com.dju.gdsc.domain.oauth.dto.TokenResponseDto;
import com.dju.gdsc.domain.oauth.entity.ProviderType;
import com.dju.gdsc.domain.oauth.entity.UserRefreshToken;
import com.dju.gdsc.domain.oauth.handler.Oauth2Handler;
import com.dju.gdsc.domain.oauth.info.OAuth2UserInfo;
import com.dju.gdsc.domain.oauth.repository.UserRefreshTokenRepository;
import com.dju.gdsc.domain.oauth.token.AuthToken;
import com.dju.gdsc.domain.oauth.token.AuthTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
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
    public void signInByOAuth(final ProviderType provider, final String code , HttpServletResponse response)  {
        OAuth2UserInfo userInfo = oauth2Handler.getUserInfo(provider, code);
        Member member = memberRepository.findByProviderTypeAndUserId(provider, userInfo.getId())
                .orElseGet(() -> customOAuth2UserService.createUser(userInfo , provider));
        TokenResponseDto token = createNewTokens(member);
        response.addHeader("token", token.getAccessToken());
        response.addHeader("refreshToken", token.getRefreshToken());
        response.addHeader("expiresIn", String.valueOf(token.getExpireIn()));

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
