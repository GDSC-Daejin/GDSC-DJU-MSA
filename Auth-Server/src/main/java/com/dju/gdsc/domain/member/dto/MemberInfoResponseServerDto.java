package com.dju.gdsc.domain.member.dto;

import com.dju.gdsc.domain.member.model.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberInfoResponseServerDto {
    private String nickname;
    private RoleType role;
    private String profileImageUrl;
}
