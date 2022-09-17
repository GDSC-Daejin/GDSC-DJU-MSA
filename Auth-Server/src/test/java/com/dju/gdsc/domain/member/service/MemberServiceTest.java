package com.dju.gdsc.domain.member.service;

import com.dju.gdsc.domain.member.entity.Member;
import com.dju.gdsc.domain.member.model.RoleType;
import com.dju.gdsc.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {
    @InjectMocks
    private MemberService memberService;
    @Mock
    private MemberRepository memberRepository;

    @Test
    void 멤버리스트() {
        Member member = Member.builder()
                .userId("test")
                .email("g@mail.com")
                .password("1234")
                .emailVerifiedYn("Y")
                .username("test")
                .role(RoleType.MEMBER)
                .profileImageUrl("test")
                .build();
        Member member2 = Member.builder()
                .userId("test2")
                .password("1234")
                .email("g")
                .emailVerifiedYn("Y")
                .username("test")
                .role(RoleType.MEMBER)
                .profileImageUrl("test")
                .build();
        //given(memberRepository.saveAll(Arrays.asList(member, member2))).willReturn(Arrays.asList(member, member2));
        when(memberRepository.findAll()).thenReturn(Arrays.asList(member, member2));
        List<Member> members = memberService.멤버리스트();
        List<Member> compareMembers = memberRepository.findAll();
        System.out.println("members = " + members);
        System.out.println("compareMembers = " + compareMembers);
        assertEquals(members.size(), compareMembers.size());
        assertEquals(members.get(0).getUserId(), compareMembers.get(0).getUserId());


    }

    @Test
    void getUserId() {
    }

    @Test
    void getMemberInfo() {
    }

    @Test
    void getMemberInfos() {
    }

    @Test
    void 정보업데이트() {
    }

    @Test
    void regularExpressionUrl() {
    }

    @Test
    void regularExpressionNickname() {
    }

    @Test
    void regularExpressionPhoneNumber() {
    }

    @Test
    void 닉네임중복검사() {
    }

    @Test
    void deleteMemberForTest() {
    }

    @Test
    void getMemberInfoByNickname() {
    }
}