package com.dju.gdsc.domain.member.controller;

import com.dju.gdsc.domain.global.controller.AbstractControllerTest;
import com.dju.gdsc.domain.global.token.WithCustomJwtFactory;
import com.dju.gdsc.domain.member.dto.MemberInfoRequestDto;
import com.dju.gdsc.domain.member.entity.Member;
import com.dju.gdsc.domain.member.entity.MemberInfo;
import com.dju.gdsc.domain.member.model.PositionType;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.annotation.PostConstruct;

import java.util.Date;

import static com.dju.gdsc.domain.member.factory.MemberEntityFactory.getMember;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    @WithMockUser(username = "test", roles = "MEMBER")
    // for Authentication  , security context holder 에 등록
    @DisplayName("내 정보 조회 with Member Security")
    void getUserV2() throws Exception {
        String token = "Bearer " + WithCustomJwtFactory.create("test", "MEMBER", 10000L);
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
    @WithMockUser(username = "test", roles = "MEMBER")
    @Transactional // for fetchType.LAZY ,  @Transactional 을 붙여야지만 영속성 컨텍스트가 유지되어서 쿼리가 나가지 않는다.
    // Member 를 조회하면서 MemberInfo 를 조회하는데, MemberInfo 는 LAZY 로 설정되어있어서 쿼리가 나가지 않는다.
    // 그래서 @Transactional 을 붙여서 영속성 컨텍스트를 유지시켜줘야 한다.
    void update() throws Exception {
        
        //given
        MemberInfoRequestDto dto = MemberInfoRequestDto.builder()
                .blogUrl("https://blog.naver.com/1234")
                .nickname("test")
                .etcUrl("https://hello.com")
                .hashTag("test").major("산경공")
                .generation(0)
                .gitEmail("test@mail.com")
                .birthday(new Date())
                .introduce("안녕하세요")
                .gitHubUrl("https://github/test")
                .positionType(PositionType.Backend)
                .studentID("20180000")
                .phoneNumber("010-1234-1234")
                .build();
        // when
        String token = "Bearer " + WithCustomJwtFactory.create("test", "MEMBER", 10000L);
        mvc.perform(put("/member-route/api/guest/v1/me")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(dto))
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andDo(print());
        // then
        MemberInfo memberInfo = memberRepository.findByUserId("test").getMemberInfo();
        assertEquals(memberInfo.getBlogUrl(), dto.getBlogUrl());
        assertEquals(memberInfo.getEtcUrl(), dto.getEtcUrl());
        assertEquals(memberInfo.getHashTag(), dto.getHashTag());
        assertEquals(memberInfo.getMajor(), dto.getMajor());
        assertEquals(memberInfo.getGeneration(), dto.getGeneration());
        assertEquals(memberInfo.getGitEmail(), dto.getGitEmail());
        assertEquals(memberInfo.getIntroduce(), dto.getIntroduce());
        assertEquals(memberInfo.getGitHubUrl(), dto.getGitHubUrl());
        assertEquals(memberInfo.getPositionType(), dto.getPositionType());
        assertEquals(memberInfo.getStudentID(), dto.getStudentID());
        assertEquals(memberInfo.getPhoneNumber(), dto.getPhoneNumber());


    }


}