package com.dju.gdsc.domain.oauth.handler;

import com.dju.gdsc.domain.oauth.entity.ProviderType;
import com.dju.gdsc.domain.oauth.info.OAuth2UserInfo;
import com.dju.gdsc.domain.oauth.infrastructure.OAuthRequester;
import lombok.RequiredArgsConstructor;
import okhttp3.internal.http2.ErrorCode;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Oauth2Handler {
    private final List<OAuthRequester> oAuthRequesters;

    public OAuth2UserInfo getUserInfo(final ProviderType provider, final String code) {
        OAuthRequester requester = getRequester(provider);
        return requester.getUserInfo(code);
    }

    private OAuthRequester getRequester(final ProviderType provider) {
        return oAuthRequesters.stream()
                .filter(requester -> requester.supports(provider))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Not implemented"));
    }

}
