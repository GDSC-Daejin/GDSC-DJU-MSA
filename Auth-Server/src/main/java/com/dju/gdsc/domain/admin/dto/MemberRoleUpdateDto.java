package com.dju.gdsc.domain.admin.dto;


import com.dju.gdsc.domain.member.model.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
public class MemberRoleUpdateDto {
    @Schema(description = "회원 아이디" , example = "1026494943134")
    private String userId;
    @Schema(description = "회원 권한" , example = "CORE")
    private RoleType role;
}
