package com.dju.gdsc.domain.oauth.handler;

import com.dju.gdsc.domain.oauth.entity.ProviderType;
import com.dju.gdsc.domain.oauth.info.OAuth2UserInfo;
import com.dju.gdsc.domain.oauth.infrastructure.OauthRequester;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Oauth2Handler {
    private final List<OauthRequester> oAuthRequesters;

    public OAuth2UserInfo getUserInfo(final ProviderType provider, final String code) {
        OauthRequester requester = getRequester(provider);
        return requester.getUserInfo(code);
    }

    private OauthRequester getRequester(final ProviderType provider) {
        return oAuthRequesters.stream()
                .filter(requester -> requester.supports(provider))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Not implemented"));
    }

}
