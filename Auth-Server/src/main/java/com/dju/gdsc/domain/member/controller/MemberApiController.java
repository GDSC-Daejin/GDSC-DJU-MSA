package com.dju.gdsc.domain.member.controller;


import com.dju.gdsc.domain.member.dto.MemberInfoRequestDto;
import com.dju.gdsc.domain.member.entity.Member;
import com.dju.gdsc.domain.member.entity.MemberInfo;
import com.dju.gdsc.domain.member.service.MemberService;
import com.dju.gdsc.domain.common.dto.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member-route")
@Tag(name = "MemberController - 멤버 컨트롤러 prefix mapping = /member-route" , description = "멤버 컨트롤러")
@Slf4j
public class MemberApiController {

    private final MemberService memberService;



    @Operation(summary = "Member 내용 보기" , description = "Member 내용 값 보기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member 내용 보기 성공" , content = @Content(schema = @Schema(implementation = Member.class))),
            @ApiResponse(responseCode = "400", description = "Member 내용 보기 실패"),
            @ApiResponse(responseCode = "401", description = "로그인 필요"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 Route"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/api/guest/v1/me")
    public Response getUserV2(@AuthenticationPrincipal User userId) {
        Member member =memberService.getUserId(userId.getUsername());
        log.info("member : {}", member);
        return Response.success("data" , member);
    }

    @Operation(summary = "Member 내용 보기" , description = "Member 내용 값 보기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member 내용 보기 성공" , content = @Content(schema = @Schema(implementation = MemberInfo.class))),
            @ApiResponse(responseCode = "400", description = "Member 내용 보기 실패"),
            @ApiResponse(responseCode = "401", description = "로그인 필요"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 Route"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/api/guest/v1/info")
    public Response getMemberInfo(@AuthenticationPrincipal User userId) {

        MemberInfo memberInfo = memberService.getUserId(userId.getUsername()).getMemberInfo();
        return Response.success("data" , memberInfo);
    }

    @Operation(summary = "유저 자기 정보 업데이트" , description = "유저 자기 정보 업데이트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 자기 정보 업데이트 성공" , content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "400", description = "유저 자기 정보 업데이트 실패"),
            @ApiResponse(responseCode = "401", description = "로그인 필요"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 Route"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PutMapping("/api/guest/v1/me")
    public Response Update(@AuthenticationPrincipal User userId , @RequestBody MemberInfoRequestDto memberInfo){
        memberService.정보업데이트(userId.getUsername(),memberInfo);
        return Response.success("message" , "SUCCESS");
    }

    @Operation(summary = "닉네임 중복검사" , description = "닉네임 중복검사")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임 중복검사 성공" , content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "400", description = "닉네임 중복검사 실패"),
            @ApiResponse(responseCode = "401", description = "로그인 필요"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 Route"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/api/guest/v1/validation/nickname")
    public Response validationNickname(@RequestBody String nickname){
        // 있으면 false return 없으면 true return
        return Response.success("data" ,!memberService.닉네임중복검사(nickname));
    }






}