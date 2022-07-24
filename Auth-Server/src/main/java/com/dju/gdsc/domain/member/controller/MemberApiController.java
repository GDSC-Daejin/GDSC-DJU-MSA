package com.dju.gdsc.domain.member.controller;


import com.dju.gdsc.domain.member.dto.MemberInfoRequestDto;
import com.dju.gdsc.domain.member.entity.Member;
import com.dju.gdsc.domain.member.entity.MemberInfo;
import com.dju.gdsc.domain.member.service.MemberService;
import com.dju.gdsc.global.dto.ApiResponse;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member-route")
public class MemberApiController {

    private final MemberService memberService;


    @ApiOperation(value = "Member 내용 보기" , notes = "Member 내용 값 보기")
    @GetMapping("/api/guest/v1/me")
    public ApiResponse getUserV2(@AuthenticationPrincipal User userId) {
        Member member =memberService.getUserId(userId.getUsername());
        return ApiResponse.success("data" , member);
    }

    @ApiOperation(value = "Member 내용 보기" , notes = "MemberInfo 내용 값 보기")
    @GetMapping("/api/guest/v1/info")
    public ApiResponse getMemberInfo(@AuthenticationPrincipal User userId) {

        MemberInfo memberInfo = memberService.getUserId(userId.getUsername()).getMemberInfo();
        return ApiResponse.success("data" , memberInfo);
    }

    @ApiOperation(value = "유저 자기 정보 업데이트" , notes = "JWT 토큰값이 들어가야 사용자를 인식 가능함")
    @PutMapping("/api/guest/v1/me")
    public ApiResponse Update(@AuthenticationPrincipal User userId , @RequestBody MemberInfoRequestDto memberInfo){
        memberService.정보업데이트(userId.getUsername(),memberInfo);
        return ApiResponse.success("message" , "SUCCESS");
    }

    @ApiOperation(value = "닉네임 중복검사" , notes = "nickname 보낸 값이 중복인지 검사 있으면 false return 없으면 true return")
    @PostMapping("/api/guest/v1/validation/nickname")
    public ApiResponse validationNickname(@RequestBody String nickname){
        // 있으면 false return 없으면 true return
        return ApiResponse.success("data" ,!memberService.닉네임중복검사(nickname));
    }




}