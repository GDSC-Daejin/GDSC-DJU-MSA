package com.dju.gdsc.domain.admin.controller;


import com.dju.gdsc.domain.admin.dto.SupportDto;
import com.dju.gdsc.domain.admin.service.SupportService;
import com.dju.gdsc.domain.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member-route/")
public class SupportApiController {
    private final SupportService supportService;
    @GetMapping("/api/v1/support/limit")
    public ResponseDto<SupportDto> list() {
        return new ResponseDto<>(HttpStatus.OK, supportService.지원제한(), "지원제한 정보");
    }
    @PutMapping("/api/admin/v1/support/limit/update")
    public ResponseDto<Integer> update(@RequestBody SupportDto supportDto){
        supportService.지원제한업데이트(supportDto);
        return new ResponseDto<>(HttpStatus.OK, 1, "업데이트 성공");
    }
}
