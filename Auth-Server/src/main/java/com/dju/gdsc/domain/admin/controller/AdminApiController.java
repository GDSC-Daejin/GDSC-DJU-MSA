package com.dju.gdsc.domain.admin.controller;


import com.dju.gdsc.domain.admin.dto.MemberRoleUpdateDto;
import com.dju.gdsc.domain.admin.dto.WarningDto;
import com.dju.gdsc.domain.admin.service.AdminService;
import com.dju.gdsc.domain.common.dto.Response;
import com.dju.gdsc.domain.member.entity.Member;
import com.dju.gdsc.domain.member.model.RoleType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/member-route/api/admin")
@RequiredArgsConstructor
public class AdminApiController {

    private final AdminService adminService;




    @Operation(summary = "사용자 정보 조회", description = "사용자 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "조회 실패")
    })
    @PutMapping("v1/update/role")
    public Response<?> updateRole(@RequestBody MemberRoleUpdateDto memberRoleUpdateDto){
        String userId = memberRoleUpdateDto.getUserId();
        RoleType role = memberRoleUpdateDto.getRole();
        adminService.맴버권한수정(userId, role);
        return Response.success("message", "Success");
    }


    @Operation(summary = "전체회원목록", description = "모든 회원을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공" ,
                    content = @Content(array = @ArraySchema( schema = @Schema(implementation = Member.class)))),
            @ApiResponse(responseCode = "400", description = "조회 실패")
    })
    @GetMapping("v1/all/list")
    public Response<List<Member>> retrieveUserList(){
        return Response.success("data", adminService.전체회원목록());
    }

    @Operation(summary = "멤버목록", description = "게스트가 아닌 멤버를 조회합니다. 전화번호 Not null 인 회원만")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공" ,
                    content = @Content(array = @ArraySchema( schema = @Schema(implementation = Member.class)))),
            @ApiResponse(responseCode = "400", description = "조회 실패")
    })
    @GetMapping("v1/member/list")
    public Response<List<Member>> retrieveMemberList(){
        return Response.success("data", adminService.멤버목록());
    }

    @Operation(summary = "게스트목록", description = "게스트를 조회합니다 Not null 인 회원만")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공" ,
                    content = @Content(array = @ArraySchema( schema = @Schema(implementation = Member.class)))),
            @ApiResponse(responseCode = "400", description = "조회 실패")
    })
    @GetMapping("v1/guest/list")
    public Response<List<Member>> retrieveGuestList(){
        return Response.success("data", adminService.게스트목록());
    }




    @Operation(summary = "관리자 경고 주기" , description = "관리자들이 멤버에게 경고를 줍니다. 로그인이 되어 있어야 합니다. ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공" ,
                    content = @Content(array = @ArraySchema( schema = @Schema(implementation = Member.class)))),
            @ApiResponse(responseCode = "400", description = "조회 실패")
    })
    @PostMapping("/v1/warning")
    public Response giveWarning(@RequestBody WarningDto warningDto , @AuthenticationPrincipal User principal) {
        adminService.경고주기(principal.getUsername() , warningDto);
        return Response.success("message", "Success");
    }

}


