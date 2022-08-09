package com.dju.gdsc.domain.member.dto;

import com.dju.gdsc.domain.member.model.PositionType;
import com.dju.gdsc.domain.member.model.RoleType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public
class MemberSlackResponseDto {
    String nickName;
    RoleType roleType;
    String slackImageUrl;
    String introduce;
    PositionType positionType;
    Integer generation;

}
