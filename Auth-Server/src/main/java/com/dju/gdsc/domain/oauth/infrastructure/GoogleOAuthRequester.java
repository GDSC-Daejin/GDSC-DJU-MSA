package com.dju.gdsc.domain.oauth.infrastructure;

import com.dju.gdsc.domain.oauth.entity.ProviderType;
import com.dju.gdsc.domain.oauth.info.OAuth2UserInfo;
import com.dju.gdsc.domain.oauth.info.impl.GoogleOAuth2UserInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import okhttp3.internal.http2.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.MultivaluedMap;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

// for android
@Component
@RequiredArgsConstructor
public class GoogleOAuthRequester implements OAuthRequester {
    @Value("${oauth2.google.url}")
    private String GOOGLE_SNS_LOGIN_URL;
    private String GOOGLE_TOKEN_REQUEST_URL = "https://oauth2.googleapis.com/token";
    @Value("${oauth2.google.client-id}")
    private String GOOGLE_SNS_CLIENT_ID;

    @Value("${oauth2.google.callback-url}")
    private String GOOGLE_SNS_CALLBACK_URL;

    @Value("${oauth2.google.client-secret}")
    private String GOOGLE_SNS_CLIENT_SECRET;

    @Value("${oauth2.google.scope}")
    private String GOOGLE_DATA_ACCESS_SCOPE;

    @Override
    public OAuth2UserInfo getUserInfo(String code) {
        String accessToken = getAccessToken(code);
        return getUserInfoByAccessToken(accessToken);
        //return new GoogleOAuth2UserInfo(new HashMap<>(Collections.singletonMap("access_token", accessToken)));
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
        Map responseBody = restTemplate
                .postForObject(GOOGLE_TOKEN_REQUEST_URL, request, MultivaluedMap.class);
        return (String) responseBody.get("access_token");
/*// 전달받은 데이터를 gson 라이브러리를 사용해서 바로 매핑시킬 수 있음
        if (response.getStatusCode() == HttpStatus.OK) {
            return gson.fromJson(response.getBody(), RetKakaoAuth.class);
        }*/



        /*Map<String, Object> responseBody = WebClient.builder()
                .baseUrl(tokenRequestUri)
                .build()
                .post()
                .uri(uriBuilder -> uriBuilder.queryParam("code", code)
                        .queryParam("client_id", GOOGLE_SNS_CLIENT_ID)
                        .queryParam("client_secret", GOOGLE_SNS_CLIENT_SECRET)
                        .queryParam("redirect_uri", GOOGLE_SNS_CALLBACK_URL)
                        .queryParam("grant_type", "authorization_code")
                        .build())
                .headers(httpHeaders -> {
                    httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                    httpHeaders.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
                })
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), response -> {
                    throw new CustomException(ErrorCode.OAUTH_REQUEST_FAILED);
                })
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .blockOptional()
                .orElseThrow(() -> new CustomException(ErrorCode.OAUTH_REQUEST_FAILED));
        if (!responseBody.containsKey("access_token")) {
            throw new CustomException(ErrorCode.OAUTH_SERVER_ERROR);
        }*/
    }

    private OAuth2UserInfo getUserInfoByAccessToken(String accessToken) {
        Map responseBody = new RestTemplate()
                .getForObject(GOOGLE_SNS_LOGIN_URL + "?access_token=" + accessToken, Map.class);
    /*    Map<String, Object> responseBody = WebClient.builder()
                .baseUrl(userRequestUri)
                .build()
                .get()
                .headers(httpHeaders -> {
                    httpHeaders.setBearerAuth(accessToken);
                    httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                    httpHeaders.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
                })
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), response -> {
                    throw new CustomException(ErrorCode.OAUTH_REQUEST_FAILED);
                })
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .blockOptional()
                .orElseThrow(() -> new CustomException(ErrorCode.OAUTH_REQUEST_FAILED));*/
        return new GoogleOAuth2UserInfo(responseBody);
    }


}
