package com.dju.gdsc.domain.oauth.service;

import com.dju.gdsc.domain.oauth.entity.ProviderType;
import com.dju.gdsc.domain.oauth.info.OAuth2UserInfo;
import com.dju.gdsc.domain.oauth.info.OAuth2UserInfoFactory;
import com.dju.gdsc.domain.oauth.infrastructure.GoogleOAuthRequester;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class Oauth2NotWebService {
   /* @Transactional
    public AllTokenResponse signInByOAuth(final ProviderType provider, final String code) {
        OAuthUserInfo userInfo = oAuthHandler.getUserInfo(provider, code);
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, user.getAttributes());
        User user = userRepository.findByProviderAndUid(userInfo.getProvider(), userInfo.getUid())
                .orElseGet(() -> signUp(userInfo));
        return createNewTokens(user);
    }*/
}
