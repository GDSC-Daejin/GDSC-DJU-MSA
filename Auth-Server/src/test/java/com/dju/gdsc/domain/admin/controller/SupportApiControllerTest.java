package com.dju.gdsc.domain.admin.controller;

import com.dju.gdsc.domain.admin.dto.SupportDto;
import com.dju.gdsc.domain.global.controller.AbstractControllerTest;
import com.dju.gdsc.domain.global.token.WithCustomJwtFactory;
import com.dju.gdsc.domain.member.entity.Member;
import com.dju.gdsc.domain.member.model.RoleType;
import com.dju.gdsc.domain.member.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SupportApiControllerTest extends AbstractControllerTest {
    @Autowired
    private WebApplicationContext context;

    private final ObjectMapper mapper = new ObjectMapper();
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

    @Test
    void list() throws Exception {
        // TODO

        mvc.perform(
                        get("/member-route/api/v1/support/limit")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.frontend").value(false))
                .andDo(print());

    }

    @Test
    @WithMockUser(username = "ADMIN_USER_ID", roles = "LEAD")
    void update() throws Exception {
        String token = WithCustomJwtFactory.create(Admin.getUserId(), "ROLE_LEAD", 10000L);
        mvc.perform(
                        put("/member-route/api/admin/v1/support/limit/update")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(new SupportDto(true, true, true, true, true, true, true)))
                )
                .andExpect(status().isOk())
                .andDo(print());
    }
}