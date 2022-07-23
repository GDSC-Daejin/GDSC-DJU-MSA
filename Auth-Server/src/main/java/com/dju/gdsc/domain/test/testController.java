package com.dju.gdsc.domain.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RefreshScope
public class testController {
    @GetMapping("/test")
    public String test(@Value("${spring.datasource.username}") String name , @Value("${spring.datasource.password}") String password){
        log.info("name : {} , password : {}",name,password);
        return name + " " + password;
    }



}
