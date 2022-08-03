package com.dju.gdsc.domain.auth.controller;

import com.dju.gdsc.domain.auth.service.LogoutService;
import com.dju.gdsc.global.utils.HeaderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LogoutController {
    private final LogoutService logoutService;
    @GetMapping("/logout")
    public ServerHttpResponse logout(ServerHttpRequest request , ServerHttpResponse response) {
        String accessToken = HeaderUtil.getAccessToken(request);
        logoutService.logout(accessToken);
        response.setStatusCode(HttpStatus.OK);
        return response;
    }
}
