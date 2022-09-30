package com.dju.gdsc.domain.admin.service;

import com.dju.gdsc.domain.member.entity.Member;
import com.dju.gdsc.domain.member.factory.MemberEntityFactory;
import com.dju.gdsc.domain.member.model.RoleType;
import com.dju.gdsc.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {
    @InjectMocks
    private AdminService adminService;
    @Mock
    private MemberRepository memberRepository;


    @Test
    void 맴버권한수정() {
        // given
        Member member = MemberEntityFactory.getMember("test", RoleType.MEMBER);
        when(memberRepository.findByUserId(member.getUserId())).thenReturn((member));
        // when
        adminService.맴버권한수정(member.getUserId(), RoleType.CORE);
        // then
        assertEquals(member.getRole(), RoleType.CORE);

    }

    @Test
    void 멤버목록() {
        // Given
        Member admin = MemberEntityFactory.getMember("admin", RoleType.LEAD);
        admin.getMemberInfo().setPhoneNumber("010-1234-5678");
        Member member = MemberEntityFactory.getMember("member", RoleType.MEMBER);
        Member core = MemberEntityFactory.getMember("core", RoleType.CORE);
        // When
        List<RoleType> roleTypes = new ArrayList<>();
        roleTypes.add(RoleType.MEMBER);
        roleTypes.add(RoleType.CORE);
        roleTypes.add(RoleType.LEAD);
        when(memberRepository.findMembersByRoleInAndMemberInfo_PhoneNumberIsNotNull(roleTypes)).thenReturn(Arrays.asList(admin));
        // Then
        List<Member> members = adminService.멤버목록();
        assertEquals(members.size(), 1);
        assertEquals(members.get(0).getUserId(), "admin");

    }

    @Test
    void 전체회원목록() {
    }

    @Test
    void 게스트목록() {
    }

    @Test
    void 경고주기() {
    }
}