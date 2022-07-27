package com.dju.gdsc.domain.admin.dto;


import com.dju.gdsc.domain.member.model.RoleType;
import lombok.Data;

@Data
public class MemberRoleUpdateDto {
    private String userId;
    private RoleType role;
}
