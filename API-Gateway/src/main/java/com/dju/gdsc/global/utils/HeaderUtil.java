package com.dju.gdsc.global.utils;

import org.springframework.http.server.reactive.ServerHttpRequest;


import java.util.Objects;

public class HeaderUtil {

    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";

    private final static String HEADER_REFRESH_TOKEN = "RefreshToken";
    public static String getAccessToken(ServerHttpRequest request) {
        String headerValue = Objects.requireNonNull(request.getHeaders().get(HEADER_AUTHORIZATION)).get(0);

        if (headerValue.startsWith(TOKEN_PREFIX)) {
            return headerValue.substring(TOKEN_PREFIX.length());
        }

        return null;
    }
    public static String getHeaderRefreshToken(ServerHttpRequest request) {
        String headerValue = Objects.requireNonNull(request.getHeaders().get(HEADER_REFRESH_TOKEN)).get(0);


        if (headerValue.startsWith(TOKEN_PREFIX)) {
            return headerValue.substring(TOKEN_PREFIX.length());
        }

        return null;
    }
}