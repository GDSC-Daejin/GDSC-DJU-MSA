package com.dju.gdsc.domain.oauth.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.util.SerializationUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class CookieUtil {
    private static final String AUTHORIZATION = "token";

    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return Optional.of(cookie);
                }
            }
        }
        return Optional.empty();
    }

    public static void addCookie(HttpServletRequest request,HttpServletResponse response, String name, String value, int maxAge) {

        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        // set allow credentials
        String domain = request.getServerName();
        log.info("domain : {}", domain);
        if(domain.contains("gdsc-dju.com")){
            cookie.setDomain("gdsc-dju.com");
            log.info("cookie domain : {}",cookie.getDomain());
            response.addCookie(cookie);
        }else{
            response.addCookie(cookie);
        }

        //cookie.setDomain("gdsc-dju.com");

    }
    public static void addCookie(String targetUrl,HttpServletResponse response, String name, String value, int maxAge) {

        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(!AUTHORIZATION.equals(name));
        cookie.setMaxAge(maxAge);
        // url 에서 도메인 추출
        String domain = targetUrl.replace("http://","").replace("https://","").split("/")[0];
        domain = domain.split(":")[0];
        log.info("domain : {}", domain);
        if(domain.contains("gdsc-dju.com")){
            cookie.setDomain("gdsc-dju.com");
            log.info("cookie domain : {}",cookie.getDomain());
            response.addCookie(cookie);
        }else {
            addCookie(response,name,value, maxAge , domain);
        }

        //cookie.setDomain("gdsc-dju.com");

    }
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge , String domain) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .sameSite("None")
                .domain(domain)
                .secure(true)
                .path("/")
                .maxAge(maxAge)
                .httpOnly(false)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }

    public static String serialize(Object obj) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(obj));
    }

    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(
                SerializationUtils.deserialize(
                        Base64.getUrlDecoder().decode(cookie.getValue())
                )
        );
    }

}
