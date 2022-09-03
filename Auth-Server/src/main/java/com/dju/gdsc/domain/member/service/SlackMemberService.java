package com.dju.gdsc.domain.member.service;

import com.dju.gdsc.domain.member.dto.MemberSlackResponseDto;
import com.dju.gdsc.domain.member.entity.Member;
import com.dju.gdsc.domain.member.entity.MemberInfo;
import com.dju.gdsc.domain.member.entity.SlackMemberInfo;
import com.dju.gdsc.domain.member.repository.JpaMemberInfoRepository;
import com.dju.gdsc.domain.member.repository.MemberRepository;
import com.dju.gdsc.domain.member.repository.SlackMemberInfoRepository;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.users.UsersListRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SlackMemberService {
    private final MethodsClient slackMethodsClient;
    private final SlackMemberInfoRepository slackMemberInfoRepository;
    private final JpaMemberInfoRepository jpaMemberInfoRepository;
    private final MemberRepository memberRepository;
    public SlackMemberService(@Value("${slack.bot.token}") String slackToken
            , SlackMemberInfoRepository slackMemberInfoRepository
            , JpaMemberInfoRepository jpaMemberInfoRepository , MemberRepository memberRepository) {
        this.slackMethodsClient = Slack.getInstance().methods(slackToken);
        this.slackMemberInfoRepository = slackMemberInfoRepository;
        this.jpaMemberInfoRepository = jpaMemberInfoRepository;
        this.memberRepository = memberRepository;
    }

    public List<SlackMemberInfo> requestGetSlackMember() throws SlackApiException, IOException {
        return slackMethodsClient.usersList(UsersListRequest.builder().build()).getMembers()
                // 나가신 분 들과 봇 제외 추출  , emailConfirmed 로 해도 될 듯
                .stream()
                .filter(member -> !member.isDeleted())
                .filter(member -> !member.isBot())
                .map(member -> SlackMemberInfo.builder()
                        .name(member.getRealName())
                        .slackUserId(member.getId())
                        .slackDisplayName(member.getProfile().getDisplayName())
                        .profileImage72(member.getProfile().getImage72())
                        .profileImage512(member.getProfile().getImage512())
                        .build()).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SlackMemberInfo> getSlackMember(){
        return slackMemberInfoRepository.findAll();
    }
    @Transactional(readOnly = true)
    public SlackMemberInfo getSlackMemberByUserId(Member userId){
        return slackMemberInfoRepository.findByUserId(userId);
    }
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "slackMember")
    public List<MemberSlackResponseDto> getMembers(){
        List<MemberInfo> memberInfoList = jpaMemberInfoRepository.findAll();
        List<SlackMemberInfo> slackMemberInfoList = slackMemberInfoRepository.findAll();
        return memberInfoList.stream()
                .filter(member -> member.getNickname() != null)
                .map(member -> {
                    SlackMemberInfo slackMemberInfo = slackMemberInfoList.stream()
                            .filter(memberInfo -> memberInfo.getUserId()
                                    .equals(member.getMember())).findFirst()
                            .orElse(null);
                    return MemberSlackResponseDto.builder()
                            .name(slackMemberInfo != null ? slackMemberInfo.getName(): "")
                            .generation(member.getGeneration())
                            .introduce(member.getIntroduce())
                            .nickName(member.getNickname())
                            .positionType(member.getPositionType())
                            .roleType(member.getMember().getRole())
                            .slackImageUrl(slackMemberInfo != null ? slackMemberInfo.getProfileImage512() : null)
                            .build();
                }).collect(Collectors.toList());
    }
    @Transactional
    @CacheEvict(value = "slackMember", allEntries = true)
    public void synchronizationSlackMemberWithServerFirst() throws SlackApiException, IOException {
        List<MemberInfo> memberInfoList = jpaMemberInfoRepository.findAll();
        List<SlackMemberInfo> requestGetSlackMemberInfoList = requestGetSlackMember();
        memberInfoList.stream().filter(memberInfo -> memberInfo.getNickname()!=null).forEach(memberInfo -> {
            requestGetSlackMemberInfoList.stream()
                    .filter(slackMemberInfo -> slackMemberInfo.getSlackDisplayName().toUpperCase()
                            .equals(memberInfo.getNickname().toUpperCase())).findFirst()
                    .ifPresent(slackMemberInfo -> {
                        slackMemberInfo.setUserId(memberInfo.getMember());
                        slackMemberInfoRepository.save(slackMemberInfo);
                    });

        });
    }
    @Transactional
    public void synchronizationSlackMemberSelf(String userId) throws SlackApiException, IOException {
        Member member = memberRepository.findByUserId(userId);
        if(member.getMemberInfo().getNickname() == null | member.getMemberInfo().getNickname().equals("")){
            throw  new IllegalArgumentException("잘못된 접근입니다.");
        }
        List<SlackMemberInfo> requestGetSlackMemberInfoList = requestGetSlackMember();
        SlackMemberInfo slackMemberInfo = requestGetSlackMemberInfoList.stream()
                .filter(memberInfo -> memberInfo.getSlackDisplayName().toUpperCase()
                        .equals(member.getMemberInfo().getNickname().toUpperCase())).findFirst()
                // 차후 throw exeption handling 필요
                .orElseThrow(()-> new IllegalArgumentException("일치하는 사용자가 존재 하지 않습니다."));
        slackMemberInfo.setUserId(member);
        slackMemberInfoRepository.save(slackMemberInfo);
    }
}
