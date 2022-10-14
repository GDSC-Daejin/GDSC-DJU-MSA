package com.dju.gdsc.domain.oauth.controller;

import com.dju.gdsc.domain.global.controller.AbstractControllerTest;
import com.dju.gdsc.domain.global.token.WithCustomJwtFactory;
import com.dju.gdsc.domain.member.entity.Member;
import com.dju.gdsc.domain.member.factory.MemberEntityFactory;
import com.dju.gdsc.domain.member.model.RoleType;
import com.dju.gdsc.domain.member.repository.MemberRepository;
import com.dju.gdsc.domain.oauth.entity.UserRefreshToken;
import com.dju.gdsc.domain.oauth.repository.UserRefreshTokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RefreshControllerTest extends AbstractControllerTest {
    @Autowired
    private MockMvc mvc;
    private ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private UserRefreshTokenRepository userRefreshTokenRepository;
    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mvc = MockMvcBuilders.webAppContextSetup(context).addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .alwaysDo(print())
                .build();
    }
    @AfterEach
    void tearDown() {
        userRefreshTokenRepository.deleteAll();
        memberRepository.deleteAll();
    }
    @Test
    void refreshToken() throws Exception {
        // Given
        Member member = MemberEntityFactory.getMember("MEM_ID", RoleType.MEMBER);
        memberRepository.save(member);
        // 입장권
        String authToken = WithCustomJwtFactory.create(member.getUserId(), member.getRole().getCode() , 0);
        // refresh 토큰
        String refreshToken = WithCustomJwtFactory.create(member.getUserId(), member.getRole().getCode() , 1000);
        // refresh 토큰 저장
        UserRefreshToken userRefreshToken = new UserRefreshToken(member.getUserId(), refreshToken);
        userRefreshTokenRepository.save(userRefreshToken);
        // When
        mvc.perform(get("/refresh")
                .header("Authorization", "Bearer " + authToken)
                .cookie(WithCustomJwtFactory.getRefreshTokenCookies(refreshToken))
        )
                .andDo(print())
                .andExpect(status().isOk());
        // Then
    }
}