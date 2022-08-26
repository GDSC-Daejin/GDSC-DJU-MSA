package com.dju.gdsc.domain.member.service;


import com.dju.gdsc.domain.member.dto.MemberInfoRequestDto;
import com.dju.gdsc.domain.member.dto.MemberInfoResponseServerDto;
import com.dju.gdsc.domain.member.entity.Member;
import com.dju.gdsc.domain.member.entity.MemberInfo;
import com.dju.gdsc.domain.member.entity.SlackMemberInfo;
import com.dju.gdsc.domain.member.exeption.UserNotFoundException;
import com.dju.gdsc.domain.member.mapper.MemberInfoPublicResponseMapping;
import com.dju.gdsc.domain.member.model.RoleType;
import com.dju.gdsc.domain.member.repository.JpaMemberInfoRepository;
import com.dju.gdsc.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    private final SlackMemberService  slackMemberService;
    private final JpaMemberInfoRepository jpaMemberInfoRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void 회원가입(Member member) {
        member.setPassword(bCryptPasswordEncoder.encode(member.getPassword()));
        member.setRole(RoleType.MEMBER);
        MemberInfo memberInfo = new MemberInfo();
        memberInfo.setMember(member);
        member.setMemberInfo(memberInfo);
        validateDuplicateUsername(member);
        memberRepository.save(member);


    }

    @Transactional(readOnly = true)
    public List<Member> 멤버리스트() {
        return memberRepository.findAll();
    }
    //중복 유저네임 확인 로직
    private void validateDuplicateUsername(Member member) {
        Member find = memberRepository.findByUserId(member.getUserId());
        if(find != null) throw new IllegalStateException("이미 존재하는 아이디 입니다.");
    }



    public Member getUserId(String userId) {
        return memberRepository.findByUserId(userId);
    }
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "memberCaching", key = "#userId" , cacheManager = "ehCacheCacheManager")
    public MemberInfoResponseServerDto getMemberInfo(String userId) {
        Member member = memberRepository.findByUserId(userId);
        SlackMemberInfo slackMemberInfo = slackMemberService.getSlackMemberByUserId(member);
        MemberInfoResponseServerDto memberInfoResponseServerDto = MemberInfoResponseServerDto.builder()
                .userId(member.getUserId())
                .nickname(member.getMemberInfo().getNickname())
                .role(member.getRole())
                .profileImageUrl(slackMemberInfo == null ? member.getProfileImageUrl() : slackMemberInfo.getProfileImage512())
                .positionType(member.getMemberInfo().getPositionType())
                .introduce(member.getMemberInfo().getIntroduce())
                .build();
        return memberInfoResponseServerDto;
    }
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "memberCaching", cacheManager = "ehCacheCacheManager")
    public List<MemberInfoResponseServerDto> getMemberInfos() {
        List<Member> members = memberRepository.findAll();
        List<SlackMemberInfo> slackMemberInfos = slackMemberService.getSlackMember();
        List<MemberInfoResponseServerDto> memberInfoResponseServerDto = members.stream().map(member ->
                slackMemberInfos.stream().filter(slackMemberInfo -> slackMemberInfo.getUserId().equals(member))
                        .map(slackMemberInfo -> MemberInfoResponseServerDto.builder()
                                .userId(member.getUserId())
                                .nickname(member.getMemberInfo().getNickname())
                                .role(member.getRole())
                                .profileImageUrl(slackMemberInfo.getProfileImage512())
                                .build())
                        .findFirst()
                        .orElse(MemberInfoResponseServerDto.builder()
                                .userId(member.getUserId())
                                .nickname(member.getMemberInfo().getNickname())
                                .role(member.getRole())
                                .profileImageUrl(member.getProfileImageUrl())
                                .build())
                ).collect(Collectors.toList());
        return memberInfoResponseServerDto;
    }
    @Transactional
    @CacheEvict(value = "memberCaching", allEntries = true)
    public void 정보업데이트(String userId , MemberInfoRequestDto requestMemberInfo){
        Member member = memberRepository.findByUserId(userId);
        if(member==null) throw new IllegalArgumentException("없는 사용자 입니다. ");
        MemberInfo memberInfo = member.getMemberInfo();

        memberInfo.setIntroduce(requestMemberInfo.getIntroduce());
        memberInfo.setBirthday(requestMemberInfo.getBirthday());
        memberInfo.setGeneration(requestMemberInfo.getGeneration());
        memberInfo.setHashTag(requestMemberInfo.getHashTag());
        // 정규식 체크 데이터 유효성 검사
        memberInfo.setGitEmail(regularExpressionEmail(requestMemberInfo.getGitEmail()) ?
                requestMemberInfo.getGitEmail() : memberInfo.getGitEmail());
        memberInfo.setPhoneNumber(regularExpressionPhoneNumber(requestMemberInfo.getPhoneNumber()) ?
                requestMemberInfo.getPhoneNumber() : memberInfo.getPhoneNumber());
        memberInfo.setNickname(regularExpressionNickname(requestMemberInfo.getNickname()) ?
                requestMemberInfo.getNickname() : memberInfo.getNickname());
        memberInfo.setMajor(requestMemberInfo.getMajor());
        memberInfo.setStudentID(requestMemberInfo.getStudentID());
        memberInfo.setPositionType(requestMemberInfo.getPositionType());
        memberInfo.setBlogUrl(requestMemberInfo.getBlogUrl());
        memberInfo.setEtcUrl(requestMemberInfo.getEtcUrl());
        memberInfo.setGitHubUrl(requestMemberInfo.getGitHubUrl());
        jpaMemberInfoRepository.save(memberInfo);


    }
    // url은 고려를 ...
    public boolean regularExpressionUrl(String url){
        return url.matches("^(https?://)?(www\\.)?([-a-z0-9]+\\.)*[-a-z0-9]+\\.[a-z]{2,}(/[-a-z0-9_\\s/?=]+)*$");
    }
    public boolean regularExpressionNickname(String nickname){
        String regex = "^[a-zA-Z]{2,10}$";
        return nickname.matches(regex);
    }
    private boolean regularExpressionEmail(String email){
        String regex = "^[a-zA-Z0-9]{2,10}@[a-zA-Z0-9]{2,10}.[a-zA-Z0-9]{2,10}$";
        return email.matches(regex);
    }
    public boolean regularExpressionPhoneNumber(String phoneNumber){
        return phoneNumber.matches("^01(?:0|1|[6-9])[-]?(\\d{3}|\\d{4})[-]?(\\d{4})$");
    }

    @Transactional
    public boolean 닉네임중복검사(String nickname){
        return memberRepository.existsByMemberInfo_Nickname(nickname);
    }

    @Transactional
    public void deleteMemberForTest(String userId){
        Member member = memberRepository.findByUserId(userId);
        memberRepository.delete(member);
    }


    public MemberInfoResponseServerDto getMemberInfoByNickname(String nickname) {
        Member member = memberRepository.findByMemberInfo_Nickname(nickname).orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));
        MemberInfoResponseServerDto memberInfoResponseServerDto = getMemberInfo(member.getUserId());
        return memberInfoResponseServerDto;
    }
}
