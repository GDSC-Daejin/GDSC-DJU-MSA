package com.dju.gdsc.domain.oauth.utils;

import com.dju.gdsc.domain.global.controller.AbstractControllerTest;
import com.dju.gdsc.domain.global.token.WithCustomJwtFactory;
import com.dju.gdsc.domain.oauth.token.AuthToken;
import com.dju.gdsc.domain.oauth.token.AuthTokenProvider;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

public class JwtCookieUtilTest extends AbstractControllerTest {
    private final static String Authorization = "token";
    @Test
    void timeFormatting() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        AuthToken jwt = WithCustomJwtFactory.createToken("test", "test", 10000);
        System.out.println(new Date(jwt.getTokenClaims().getExpiration().getTime()));
        System.out.println(sdf.format(jwt.getTokenClaims().get("exp")));
        System.out.println(sdf.format(jwt.getTokenClaims().getExpiration()));
        System.out.println(sdf.format(jwt.getTokenClaims().getExpiration().getTime()));
        assertEquals(sdf.format(jwt.getTokenClaims().getExpiration()), sdf.format(jwt.getTokenClaims().getExpiration().getTime()));
    }
}