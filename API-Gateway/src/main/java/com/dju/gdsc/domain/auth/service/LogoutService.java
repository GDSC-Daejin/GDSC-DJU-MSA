package com.dju.gdsc.domain.auth.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class LogoutService {
    @Cacheable(key = "#accessToken" , cacheNames = "token-cache")
    public String logout(String accessToken) {
        // TODO
        return null;
    }
    @CacheEvict(key = "#accessToken" , cacheNames = "token-cache")
    public void logIn(String accessToken) {
        // TODO
    }

}
