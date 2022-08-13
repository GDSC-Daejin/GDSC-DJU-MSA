package com.dju.gdsc.domain.member.controller;

import com.dju.gdsc.domain.common.dto.Response;
import com.dju.gdsc.domain.member.entity.SlackMemberInfo;
import com.dju.gdsc.domain.member.service.SlackMemberService;
import com.slack.api.methods.SlackApiException;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/member-route/")
public class SlackMemberApiController {
    private final SlackMemberService slackMemberService;
    public SlackMemberApiController(SlackMemberService slackMemberService) {
        this.slackMemberService = slackMemberService;
    }
    @Operation(summary = "슬랙 사용자 정보 불러오기" , description = "슬랙 사용자 정보 불러오기 google 로그인 후 슬랙 유저와 동기화 한 유저만")
    @GetMapping("/api/v1/slackMember")
    public Response getSlackMember()  {
        return Response.success("data" , slackMemberService.getSlackMember());
    }

    @Operation(summary = "슬랙 사용자 정보 불러오기" , description = "슬랙 사용자 정보 불러오기 google 로그인 후 슬랙 유저와 동기화 한 유저만")
    @GetMapping("/api/v1/members")

    public Response getMember()  {
        return Response.success("data" , slackMemberService.getMembers());
    }
    @Operation(summary = "슬랙 사용자 정보 전체 동기화" , description = "모든 사용자 정보를 슬랙 정보와 매칭 시키는 작업 , 닉네임으로 매칭 시킴")
    @PostMapping("/api/admin/v1/slackMember")
    public Response postSlackMember() throws  SlackApiException, IOException {
        slackMemberService.synchronizationSlackMemberWithServerFirst();
        return Response.success("message" , HttpStatus.OK);
    }
    @Operation(summary = "슬랙 사용자 정보 스스로 동기화" , description = "사용자 정보를 슬랙 정보와 매칭 시키는 작업 , 닉네임으로 매칭 시킴")
    @PostMapping("/api/guest/v1/slackMember/self")
    public Response postSlackMember(@AuthenticationPrincipal User principal) throws  SlackApiException, IOException {
        slackMemberService.synchronizationSlackMemberSelf(principal.getUsername());
        return Response.success("message" , HttpStatus.OK);
    }
}
