package com.dju.gdsc.domain.common;

import com.dju.gdsc.domain.auth.service.LogoutService;
import com.dju.gdsc.global.token.AuthToken;
import com.dju.gdsc.global.token.AuthTokenProvider;
import com.dju.gdsc.global.utils.HeaderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
@Component
@Slf4j
public class AuthenticationForAdminFilter extends AbstractGatewayFilterFactory<AuthenticationForAdminFilter.Config> {
    private final AuthTokenProvider authTokenProvider;
    private final LogoutService logoutService;

    public AuthenticationForAdminFilter(AuthTokenProvider authTokenProvider, LogoutService logoutService) {
        super(AuthenticationForAdminFilter.Config.class);
        this.authTokenProvider = authTokenProvider;
        this.logoutService = logoutService;
    }



    @Override
    public GatewayFilter apply(AuthenticationForAdminFilter.Config config) {
        return (exchange, chain) -> {

            ServerHttpRequest request = exchange.getRequest();
            if(!request.getHeaders().containsKey("Authorization")){
                return handleUnAuthorized(exchange);
            }
            String accessToken = HeaderUtil.getAccessToken(request);
            AuthToken authToken = authTokenProvider.convertAuthToken(accessToken);;
            if (!authToken.validate()) {
                exchange.getResponse().setStatusCode(HttpStatus.valueOf(401));
                return exchange.getResponse().setComplete();
            }
            if(logoutService.logout(accessToken) != null){
                return handleUnAuthorized(exchange);
            }
            logoutService.logIn(accessToken);


            if(!authToken.getTokenClaims().get("role").equals("LEAD") | !authToken.getTokenClaims().get("role").equals("CORE")) {
                exchange.getResponse().setStatusCode(HttpStatus.valueOf(403));
                log.info("User is not admin , REQUEST ROLE " + authToken.getTokenClaims().get("role")
                        + " REQUEST USER ID:  " + authToken.getTokenClaims().get("sub"));
                return exchange.getResponse().setComplete();
            }
            return chain.filter(exchange);

        };
    }
    private Mono<Void> handleUnAuthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }


    public static class Config {

    }
}