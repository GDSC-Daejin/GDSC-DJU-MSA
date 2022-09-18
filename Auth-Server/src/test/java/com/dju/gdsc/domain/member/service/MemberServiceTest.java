package com.dju.gdsc.domain.member.service;

import com.dju.gdsc.domain.member.dto.MemberInfoRequestDto;
import com.dju.gdsc.domain.member.dto.MemberInfoResponseServerDto;
import com.dju.gdsc.domain.member.entity.Member;
import com.dju.gdsc.domain.member.entity.MemberInfo;
import com.dju.gdsc.domain.member.exeption.UserNotFoundException;
import com.dju.gdsc.domain.member.model.RoleType;
import com.dju.gdsc.domain.member.repository.JpaMemberInfoRepository;
import com.dju.gdsc.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {
    @InjectMocks
    private MemberService memberService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private JpaMemberInfoRepository jpaMemberInfoRepository;

    private static Member getMember(String userId) {
        Member m = Member.builder()
                .userId(userId)
                .email(userId + "@mail.com")
                .password("1234")
                .emailVerifiedYn("Y")
                .username(userId + "test")
                .role(RoleType.MEMBER)
                .profileImageUrl("test")
                .build();
        MemberInfo mi = MemberInfo.builder()
                .nickname(userId)
                .build();
        mi.setMember(m);
        m.setMemberInfo(mi);
        return m;
    }

    @Test
    @DisplayName("회원 목록 조회")
    void 멤버리스트() {
        Member member = getMember("test");
        Member member2 = getMember("test2");
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
        Member member = getMember("test");
        when(memberRepository.findByUserId(member.getUserId())).thenReturn(member);
        Member findMember = memberService.getUserId(member.getUserId());
        assertEquals(member.getUserId(), findMember.getUserId());
    }

    @Test
    void getMemberInfo() {
        Member member = getMember("test");
        when(memberRepository.findByUserId(member.getUserId())).thenReturn(member);
        MemberInfoResponseServerDto findMember = memberService.getMemberInfo(member.getUserId());
        assertEquals(member.getUserId(), findMember.getUserId());
        System.out.println("findMember = " + findMember);

    }

    @Test
    void getMemberInfos() {
    }

    @Test
    @DisplayName("Service : Member Info update")
    void 정보업데이트() {
        // Given
        Member member = getMember("test");
        MemberInfoRequestDto memberInfoRequestDto = MemberInfoRequestDto.builder()
                .blogUrl("https://blog.naver.com/test")
                .etcUrl("test")
                .major("test")
                .hashTag("test")
                .generation(0)
                .gitEmail("test")
                .phoneNumber("010-1234-1234")
                .nickname("test")
                .introduce("test")
                .build();
        when(jpaMemberInfoRepository.save(any(MemberInfo.class))).thenReturn(member.getMemberInfo());
        when(memberRepository.findByUserId(member.getUserId())).thenReturn(member);
        // When
        memberService.정보업데이트(member.getUserId(), memberInfoRequestDto);
        // Then
        Member findMember = memberRepository.findByUserId(member.getUserId());
        assertEquals(memberInfoRequestDto.getBlogUrl(), findMember.getMemberInfo().getBlogUrl());
        assertEquals(memberInfoRequestDto.getEtcUrl(), findMember.getMemberInfo().getEtcUrl());
        assertEquals(memberInfoRequestDto.getMajor(), findMember.getMemberInfo().getMajor());
        assertEquals(memberInfoRequestDto.getHashTag(), findMember.getMemberInfo().getHashTag());
        assertEquals(memberInfoRequestDto.getGeneration(), findMember.getMemberInfo().getGeneration());
        assertEquals(memberInfoRequestDto.getNickname(), findMember.getMemberInfo().getNickname());
        assertEquals(memberInfoRequestDto.getIntroduce(), findMember.getMemberInfo().getIntroduce());
        assertNotEquals(memberInfoRequestDto.getGitEmail(), findMember.getMemberInfo().getGitEmail());
        System.out.println("findMember = " + findMember.getMemberInfo());
        System.out.println("memberInfoRequestDto = " + memberInfoRequestDto);

    }

    @Test
    @DisplayName("validation nickname test")
    void 닉네임중복검사() {
        // 중복이면 false return , 중복이 아니면 true return
        // Given

        Member member = getMember("test");
        memberRepository.save(member);
        // When
        boolean result = memberService.닉네임중복검사(member.getMemberInfo().getNickname());
        // Then
        assertFalse(result);

    }


    @Test
    void getMemberInfoByNickname() {
        // Given
        Member member = getMember("test");
        when(memberRepository.findByMemberInfo_Nickname("test")).thenReturn(Optional.of(member));
        when(memberRepository.findByUserId(member.getUserId())).thenReturn(member);
        // When
        MemberInfoResponseServerDto memberInfoResponseServerDto = memberService.getMemberInfoByNickname(member.getMemberInfo().getNickname());
        // Then
        assertEquals(member.getMemberInfo().getNickname(), memberInfoResponseServerDto.getNickname());
        System.out.println("memberInfoResponseServerDto = " + memberInfoResponseServerDto);

    }
}