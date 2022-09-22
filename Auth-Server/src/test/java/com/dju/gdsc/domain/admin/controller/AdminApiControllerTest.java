package com.dju.gdsc.domain.admin.controller;

import com.dju.gdsc.domain.admin.dto.MemberRoleUpdateDto;
import com.dju.gdsc.domain.global.controller.AbstractControllerTest;
import com.dju.gdsc.domain.global.token.WithCustomJwtFactory;
import com.dju.gdsc.domain.member.entity.Member;
import com.dju.gdsc.domain.member.model.RoleType;
import com.dju.gdsc.domain.member.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static com.dju.gdsc.domain.member.factory.MemberEntityFactory.getMember;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("관리자 API 테스트")
class AdminApiControllerTest extends AbstractControllerTest {
    @Autowired
    private MockMvc mvc;
    private ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MemberRepository memberRepository;
    private Member Admin;

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mvc = MockMvcBuilders.webAppContextSetup(context).addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .alwaysDo(print())
                .build();
        Admin = getMember("ADMIN_USER_ID", RoleType.LEAD);
        memberRepository.save(Admin);
    }
    @AfterEach
    void tearDown() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("API : Update Role  관리자가 회원 권한을 변경할 수 있다.")
    @WithMockUser(username = "ADMIN_USER_ID", roles = "LEAD")
    void updateRole() throws Exception {
        // given
        Member member = getMember("GUEST_USER_ID", RoleType.GUEST);
        memberRepository.save(member);
        MemberRoleUpdateDto memberRoleUpdateDto = MemberRoleUpdateDto.builder()
                .userId(member.getUserId())
                .role(RoleType.MEMBER)
                .build();
        // Token
        String accessToken = WithCustomJwtFactory.create(Admin.getUserId(), Admin.getRole().getCode(), 10000L);
        //when
        mvc.perform(
                        put("/member-route/api/admin/v1/update/role")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(memberRoleUpdateDto))
                )
                .andExpect(status().isOk());
        //then
        Member findMember = memberRepository.findByUserId(member.getUserId());
        System.out.println(findMember);
        assertEquals(RoleType.MEMBER, findMember.getRole());
    }

    @Test
    void retrieveUserList() {
    }

    @Test
    void retrieveMemberList() {
    }

    @Test
    void retrieveGuestList() {
    }

    @Test
    void giveWarning() {
    }
}