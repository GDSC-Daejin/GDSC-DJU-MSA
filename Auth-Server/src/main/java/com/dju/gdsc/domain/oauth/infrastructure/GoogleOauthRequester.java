package com.dju.gdsc.domain.oauth.infrastructure;

import com.dju.gdsc.domain.oauth.entity.ProviderType;
import com.dju.gdsc.domain.oauth.exception.Oauth2UserInfoNotFoundExeption;
import com.dju.gdsc.domain.oauth.info.OAuth2UserInfo;
import com.dju.gdsc.domain.oauth.info.impl.GoogleOAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.MultivaluedMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// for android
@Component
@RequiredArgsConstructor
public class GoogleOauthRequester implements OauthRequester {
    @Value("${oauth2.google.code.url}")
    private String GOOGLE_SNS_LOGIN_URL;
    @Value("${oauth2.google.token.url}")
    private String GOOGLE_TOKEN_REQUEST_URL;
    @Value("${oauth2.google.info.url}")
    private String GOOGLE_SNS_INFO_URL;

    @Value("${oauth2.google.client-id}")
    private String GOOGLE_SNS_CLIENT_ID;

    @Value("${oauth2.google.callback-url}")
    private String GOOGLE_SNS_CALLBACK_URL;

    @Value("${oauth2.google.client-secret}")
    private String GOOGLE_SNS_CLIENT_SECRET;



    @Override
    public OAuth2UserInfo getUserInfo(String code) {
        String accessToken = getAccessToken(code);
        return getUserInfoByAccessToken(accessToken);
    }
    @Override
    public boolean supports(ProviderType provider) {
        return provider.equals(ProviderType.GOOGLE);
    }
    // error 처리 필요
    private String getAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        // 헤더 만들기
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("client_id", GOOGLE_SNS_CLIENT_ID);
        params.put("client_secret", GOOGLE_SNS_CLIENT_SECRET);
        params.put("redirect_uri", GOOGLE_SNS_CALLBACK_URL);
        params.put("grant_type", "authorization_code");


        HttpEntity<Map<String, Object>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.exchange(GOOGLE_TOKEN_REQUEST_URL, HttpMethod.POST, request, Map.class);
        //response.getBody().forEach((k, v) -> System.out.println(k + " : " + v));
        if(response.getStatusCode() == HttpStatus.OK) {
            return response.getBody().get("access_token").toString();
        } else {
            throw new Oauth2UserInfoNotFoundExeption("Invalid Code exception");
        }

    }

    private OAuth2UserInfo getUserInfoByAccessToken(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(null, headers);
        headers.add("Authorization", "Bearer " + accessToken);
        ResponseEntity<Map> response = new RestTemplate().exchange(GOOGLE_SNS_INFO_URL, HttpMethod.GET, request, Map.class);
        if(response.getStatusCode() == HttpStatus.OK) {
            response.getBody().put("sub" , response.getBody().get("id"));
            return new GoogleOAuth2UserInfo(response.getBody());
        } else {
            throw new Oauth2UserInfoNotFoundExeption("USER INFO NOT FOUND");
        }

    }


}
