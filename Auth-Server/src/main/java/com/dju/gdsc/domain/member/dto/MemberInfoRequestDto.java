package com.dju.gdsc.domain.member.dto;

import com.dju.gdsc.domain.member.model.PositionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MemberInfoRequestDto {

    @Schema(description = "기수" , example = "1")
    private Integer generation;
    @Schema(description = "소개" , example = "안녕하세요 잘부탁드립니다.")
    private String introduce;
    @Schema(description = "영어이름" , example = "Jason")
    private String nickname;
    @Schema(description = "전번" , example = "010-9132-1234")
    private String phoneNumber;
    @Schema(description = "전공" , example = "컴퓨터공학")
    private String major;
    @Schema(description = "깃허브 이메일?" , example = "gudcks305@gmail.com")
    private String gitEmail;
    @Schema(description = "학번" , example = "20171245")
    private String studentID;
    @Schema(description = "멤버인지 코어인지 리드인지" , example = "CORE")
    private PositionType positionType;
    private String hashTag;
    private String gitHubUrl;
    private String blogUrl;
    private String etcUrl;


    private LocalDateTime birthday;


}
