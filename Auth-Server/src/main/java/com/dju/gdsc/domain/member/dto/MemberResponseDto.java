package com.dju.gdsc.domain.member.dto;

import lombok.Data;

@Data
public class MemberResponseDto {
    private String profileImageUrl;

    public MemberResponseDto(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
