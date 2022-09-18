package com.dju.gdsc.domain.member.controller;

import com.dju.gdsc.domain.global.controller.AbstractControllerTest;
import com.dju.gdsc.domain.global.token.WithCustomJwtFactory;
import com.dju.gdsc.domain.member.entity.Member;
import com.dju.gdsc.domain.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.annotation.PostConstruct;

import static com.dju.gdsc.domain.member.factory.MemberEntityFactory.getMember;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



public class MemberApiControllerTest extends AbstractControllerTest {
    @Autowired
    private MockMvc mvc;
    private ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mvc = MockMvcBuilders.webAppContextSetup(context).addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .alwaysDo(print())
                .build();
        Member memberRole = getMember("test");
        memberRepository.save(memberRole);
    }

    @AfterEach
    void tearDown() {
        memberRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "test",roles = "MEMBER")
    @DisplayName("내 정보 조회 with Member Security")
    void getUserV2() throws Exception {
        String token = "Bearer " + WithCustomJwtFactory.create("test" , "MEMBER" , 1000000L);
        mvc.perform(get("/member-route/api/guest/v1/me")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("body.data.userId").value("test"))
                .andDo(print());
    }
    @Test
    @DisplayName("내 정보 조회 without Security")
    void getUserV2WithoutSecurity() throws Exception {
        mvc.perform(get("/member-route/api/guest/v1/me"))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }


    @Test
    void getMemberInfo() {
    }

    @Test
    void update() {
    }

    @Test
    void validationNickname() {
    }
}