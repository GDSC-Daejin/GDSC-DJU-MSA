package com.dju.gdsc.domain.oauth.infrastructure;

import com.dju.gdsc.domain.oauth.entity.ProviderType;
import com.dju.gdsc.domain.oauth.handler.info.OAuth2UserInfo;

public interface OauthRequester {
    boolean supports(ProviderType provider);

    OAuth2UserInfo getUserInfo(String code);
}
