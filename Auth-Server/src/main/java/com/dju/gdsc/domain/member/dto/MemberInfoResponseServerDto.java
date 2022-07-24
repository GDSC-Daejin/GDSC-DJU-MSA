package com.dju.gdsc.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberInfoResponseServerDto {
    private String nickname;
    private String profileImageUrl;
}
