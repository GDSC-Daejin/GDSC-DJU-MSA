package com.dju.gdsc.domain.oauth.utils;

import com.dju.gdsc.domain.oauth.token.AuthToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class JwtCookieUtil {
   private final static String Authorization = "token";
   public static void authCookieGenerate(HttpServletRequest request , HttpServletResponse response , AuthToken authToken , int cookieExpiry ){
      CookieUtil.deleteCookie(request, response, Authorization);
      CookieUtil.addCookie(request,response, Authorization, authToken.getToken(), cookieExpiry);
      CookieUtil.deleteCookie(request, response, "expires_in");
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
      sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
      CookieUtil.addCookie(request, response,
              "expires_in" ,
              sdf.format(authToken.getTokenClaims().get("exp")),
              cookieExpiry);

   }
}
