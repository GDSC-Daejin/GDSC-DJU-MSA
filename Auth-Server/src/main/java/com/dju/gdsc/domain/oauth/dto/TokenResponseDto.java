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
    private long expireIn;
}
