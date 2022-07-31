package com.dju.gdsc.domain.member.dto;

import com.dju.gdsc.domain.member.model.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberInfoResponseServerDto {
    @Schema(description = "회원 아이디" , example = "1026494943134")
    private String userId;
    @Schema(description = "회원 영어이름" , example = "Jason")
    private String nickname;
    @Schema(description = "회원 권한" , example = "CORE")
    private RoleType role;
    @Schema(description = "회원 프로필 이미지 링크" , example = "https://www.google.com/~~~~~")
    private String profileImageUrl;
}
