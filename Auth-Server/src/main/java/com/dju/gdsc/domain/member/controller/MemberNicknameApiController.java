package com.dju.gdsc.domain.member.controller;

import com.dju.gdsc.domain.member.service.MemberService;
import com.dju.gdsc.global.dto.ApiResponse;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/member")
public class MemberNicknameApiController {
    private final MemberService memberService;
    @ApiOperation(value = "닉네임 불러오기" , notes = "닉네임을 불러와서 다른 서비스에 바인딩 하기 위해서")
    @GetMapping("/api/nickname/{userId}")
    public ApiResponse returnNickname(@PathVariable String userId){
        if(memberService.getUserId(userId) == null){
            return ApiResponse.fail("message" , "존재하지 않는 유저입니다.");
        }
        return ApiResponse.success("data" ,memberService.getUserId(userId).getMemberInfo().getNickname());
    }
}
