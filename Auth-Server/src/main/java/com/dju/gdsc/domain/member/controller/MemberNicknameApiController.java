package com.dju.gdsc.domain.member.controller;

import com.dju.gdsc.domain.member.dto.MemberInfoResponseServerDto;
import com.dju.gdsc.domain.member.service.MemberService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/member")
@Slf4j
public class MemberNicknameApiController {
    private final MemberService memberService;
    @ApiOperation(value = "닉네임 불러오기" , notes = "닉네임을 불러와서 다른 서비스에 바인딩 하기 위해서")
    @GetMapping("/api/memberInfo/{userId}")
    @Cacheable(key = "#userId" , cacheNames = "memberCaching")
    public MemberInfoResponseServerDto returnNickname(@PathVariable String userId){
        log.info("호출");
        if(memberService.getUserId(userId) == null){
            return null;
        }
        MemberInfoResponseServerDto memberInfo = memberService.getMemberInfo(userId);
        return memberInfo;
    }

    @ApiOperation(value = "닉네임 불러오기" , notes = "닉네임을 불러와서 다른 서비스에 바인딩 하기 위해서")
    @GetMapping("/api/memberInfo")
    @Cacheable(cacheNames = "memberCaching")
    public List<MemberInfoResponseServerDto> returnNicknameList(){
        log.info("호출");
        List<MemberInfoResponseServerDto> memberInfos = memberService.getMemberInfos();
        return memberInfos;
    }
}
