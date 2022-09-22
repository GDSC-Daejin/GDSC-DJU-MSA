package com.dju.gdsc.domain.member.repository;

import com.dju.gdsc.domain.member.entity.Member;
import com.dju.gdsc.domain.member.entity.MemberInfo;
import com.dju.gdsc.domain.member.entity.SlackMemberInfo;
import com.dju.gdsc.domain.member.model.RoleType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
public class MemberRepositoryImplTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private JpaMemberInfoRepository jpaMemberInfoRepository;
    @Autowired
    private SlackMemberInfoRepository slackMemberInfoRepository;
    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("slack , Member 테이블 조인 확인")
    void findByUserId() {
        Member member = Member.builder()
                .userId("test")
                .email("g@mail.com")
                .password("1234")
                .emailVerifiedYn("Y")
                .username("test")
                .role(RoleType.MEMBER)
                .profileImageUrl("test")
                .build();
        MemberInfo memberInfo = MemberInfo.builder()
                .member(member)
                .build();
        Member savedMember = memberRepository.save(member);
        jpaMemberInfoRepository.save(memberInfo);

        SlackMemberInfo slackMemberInfo = SlackMemberInfo.builder()
                .slackUserId("test")
                .userId(savedMember)
                .profileImage512("testSlack")
                .build();
        slackMemberInfoRepository.save(slackMemberInfo);

        Member findMember = memberRepository.findByUserIdWithSlack(savedMember.getUserId());
        assertEquals(findMember.getUserId(), savedMember.getUserId());
        assertEquals(findMember.getProfileImageUrl(), slackMemberInfo.getProfileImage512());
        assertThrows(InvalidDataAccessApiUsageException.class, () -> memberRepository.findByUserIdWithSlack
                ("test2"));



    }
}