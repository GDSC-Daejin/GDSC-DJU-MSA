package com.dju.gdsc.domain.member.controller;

import com.dju.gdsc.domain.member.dto.MemberInfoResponseServerDto;
import com.dju.gdsc.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "닉네임 불러오기 Client 에서 호출 할 일 없음" , description = "닉네임을 불러와서 다른 서비스에 바인딩 하기 위해서")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임 불러오기 성공" ,
                    content = @Content(schema = @Schema(implementation = MemberInfoResponseServerDto.class))),
            @ApiResponse(responseCode = "400", description = "닉네임 불러오기 실패")
    })
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

    @Operation(summary = "닉네임 불러오기" , description = "닉네임을 불러와서 다른 서비스에 바인딩 하기 위해서")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임 불러오기 성공" ,
                    content = @Content(array =  @ArraySchema(schema = @Schema(implementation = MemberInfoResponseServerDto.class)))),
            @ApiResponse(responseCode = "400", description = "닉네임 불러오기 실패")
    })
    @GetMapping("/api/memberInfo")
    @Cacheable(cacheNames = "memberCaching")
    public List<MemberInfoResponseServerDto> returnNicknameList(){
        log.info("호출");
        List<MemberInfoResponseServerDto> memberInfos = memberService.getMemberInfos();
        return memberInfos;
    }
}
