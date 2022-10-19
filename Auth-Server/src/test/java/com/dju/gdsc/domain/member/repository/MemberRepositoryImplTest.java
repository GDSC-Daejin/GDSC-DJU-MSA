package com.dju.gdsc.domain.member.repository;

import com.dju.gdsc.domain.member.entity.Member;
import com.dju.gdsc.domain.member.entity.MemberInfo;
import com.dju.gdsc.domain.member.entity.SlackMemberInfo;
import com.dju.gdsc.domain.member.factory.MemberEntityFactory;
import com.dju.gdsc.domain.member.model.RoleType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.util.Arrays;
import java.util.List;

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

    @Test
    void findMembersWithSlack() {
        // given
        Member member = MemberEntityFactory.getMember("test", RoleType.MEMBER);
        Member member2 = MemberEntityFactory.getMember("test2", RoleType.MEMBER);
        Member member3 = MemberEntityFactory.getMember("test3", RoleType.MEMBER);
        SlackMemberInfo slackMemberInfo = SlackMemberInfo.builder()
                .slackUserId("test")
                .userId(member)
                .profileImage512("testSlack")
                .build();
        SlackMemberInfo slackMemberInfo2 = SlackMemberInfo.builder()
                .slackUserId("test2")
                .userId(member2)
                .profileImage512("testSlack2")
                .build();
        //두 개의 정보는 슬랙 정보를 가진 멤버 , 하나는 가지지 않은 멤버
        memberRepository.saveAll(Arrays.asList(member, member2, member3));
        slackMemberInfoRepository.saveAll(Arrays.asList(slackMemberInfo, slackMemberInfo2));

        // when
        // 슬랙 정보를 가진 멤버들을 조회
        List<Member> members = memberRepository.findMembersWithSlack();

        // then
        // left join 이기 때문에 다 가져온다.
        assertEquals(members.size(), 3);
        assertEquals(members.get(0).getUserId(), member.getUserId());
        assertEquals(members.get(1).getUserId(), member2.getUserId());
        assertEquals(members.get(2).getUserId(), member3.getUserId());
        assertEquals(members.get(0).getProfileImageUrl(), slackMemberInfo.getProfileImage512());
        assertEquals(members.get(1).getProfileImageUrl(), slackMemberInfo2.getProfileImage512());
        assertEquals(members.get(2).getProfileImageUrl(), member3.getProfileImageUrl());

    }
}