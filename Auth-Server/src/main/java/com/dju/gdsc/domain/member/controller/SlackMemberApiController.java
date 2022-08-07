package com.dju.gdsc.domain.member.controller;

import com.dju.gdsc.domain.common.dto.Response;
import com.dju.gdsc.domain.member.entity.SlackMemberInfo;
import com.dju.gdsc.domain.member.service.SlackMemberService;
import com.slack.api.methods.SlackApiException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class SlackMemberApiController {
    private final SlackMemberService slackMemberService;
    public SlackMemberApiController(SlackMemberService slackMemberService) {
        this.slackMemberService = slackMemberService;
    }
    @GetMapping("/api/member/v1/slackMember")
    public List<SlackMemberInfo> getSlackMember() throws  SlackApiException, IOException {
        return slackMemberService.requestGetSlackMember();
    }
    @PostMapping("/api/member/v1/slackMember")
    public Response postSlackMember() throws  SlackApiException, IOException {
        slackMemberService.synchronizationSlackMemberWithServerFirst();
        return Response.success("message" , HttpStatus.OK);
    }
}
