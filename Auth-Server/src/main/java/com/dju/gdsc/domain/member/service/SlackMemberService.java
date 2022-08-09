package com.dju.gdsc.domain.member.service;

import com.dju.gdsc.domain.member.dto.MemberSlackResponseDto;
import com.dju.gdsc.domain.member.entity.MemberInfo;
import com.dju.gdsc.domain.member.entity.SlackMemberInfo;
import com.dju.gdsc.domain.member.repository.JpaMemberInfoRepository;
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
import java.util.stream.Collectors;

@Service
public class SlackMemberService {
    private final MethodsClient slackMethodsClient;
    private final SlackMemberInfoRepository slackMemberInfoRepository;
    private final JpaMemberInfoRepository jpaMemberInfoRepository;
    public SlackMemberService(@Value("${slack.bot.token}") String slackToken
            , SlackMemberInfoRepository slackMemberInfoRepository
            , JpaMemberInfoRepository jpaMemberInfoRepository) {
        this.slackMethodsClient = Slack.getInstance().methods(slackToken);
        this.slackMemberInfoRepository = slackMemberInfoRepository;
        this.jpaMemberInfoRepository = jpaMemberInfoRepository;
    }

    public List<SlackMemberInfo> requestGetSlackMember() throws SlackApiException, IOException {
        return slackMethodsClient.usersList(UsersListRequest.builder().build()).getMembers()
                // 나가신 분 들과 봇 제외 추출  , emailConfirmed 로 해도 될 듯
                .stream()
                .filter(member -> !member.isDeleted())
                .filter(member -> !member.isBot())
                .map(member -> SlackMemberInfo.builder()
                        .slackUserId(member.getId())
                        .slackDisplayName(member.getProfile().getDisplayName())
                        .profileImage72(member.getProfile().getImage72())
                        .profileImage512(member.getProfile().getImage512())
                        .build()).collect(Collectors.toList());
    }
    // 첫번째 slack 연결 후 update 정보 동기화 메소드
    /*public void fetchSlackMember() throws SlackApiException, IOException {
        List<SlackMemberInfo> slackMemberInfoList = requestGetSlackMember();
        slackMemberInfoRepository.findAll().forEach(member -> {
            if(member.getUserId() != null) {
                slackMemberInfoList.stream()
                        .filter(memberInfo -> memberInfo.getSlackUserId()
                                .equals(member.getSlackUserId())).findFirst()
                        .ifPresent(memberInfo -> {
                            member.setSlackDisplayName(memberInfo.getSlackDisplayName());
                            member.setProfileImage72(memberInfo.getProfileImage72());
                            member.setProfileImage512(memberInfo.getProfileImage512());
                });
            }
        });
    }*/
    @Transactional(readOnly = true)
    public List<SlackMemberInfo> getSlackMember(){
        return slackMemberInfoRepository.findAll();
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
                            .name(member.getMember().getUsername())
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
}
