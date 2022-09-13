package com.dju.gdsc.domain.oauth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class TokenResponseDto {
    private String accessToken;
    private String refreshToken;
    private long access_token_expireIn;
    private long refresh_token_expireIn;
}
