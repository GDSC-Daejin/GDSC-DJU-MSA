package com.dju.gdsc.domain.oauth.controller;

import com.dju.gdsc.domain.common.dto.Response;
import com.dju.gdsc.domain.oauth.entity.ProviderType;
import com.dju.gdsc.domain.oauth.service.Oauth2NotWebService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class Oauth2Controller {
    private final Oauth2NotWebService oauth2NotWebService;
    @GetMapping("/auth/{provider}") //GOOGLE이 들어올 것이다.
    public Response socialLoginRedirect(@PathVariable String provider , @RequestParam String code ) throws IOException {
        ProviderType providerType = ProviderType.valueOf(provider.toUpperCase());
        return Response.success("data", oauth2NotWebService.signInByOAuth(providerType, code));
    }
}
