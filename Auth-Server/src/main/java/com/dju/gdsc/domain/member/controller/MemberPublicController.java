package com.dju.gdsc.domain.member.controller;

import com.dju.gdsc.domain.common.dto.Response;
import com.dju.gdsc.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member-route")
public class MemberPublicController {
    private final MemberService memberService;

    @GetMapping("/api/v1/memberInfo/{nickname}")
    @Cacheable(key = "#nickname" , cacheNames = "memberCaching")
    public Response getMemberInfoByNickname(@PathVariable String nickname) {
        return Response.success("data", memberService.getMemberInfoByNickname(nickname));
    }

}
