
package com.dju.gdsc.domain.admin.service;

import com.dju.gdsc.domain.admin.dto.WarningDto;
import com.dju.gdsc.domain.admin.entity.WarnDescription;
import com.dju.gdsc.domain.admin.repository.JpaWarnDescription;
import com.dju.gdsc.domain.member.entity.Member;
import com.dju.gdsc.domain.member.model.RoleType;
import com.dju.gdsc.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j // 디버그를 위한 로그 설정
@Service
@RequiredArgsConstructor
public class AdminService {

    private final MemberRepository repository;
    private final JpaWarnDescription jpaWarnDescription;
    // 영속성 컨텍스트에 등록
    @Transactional
    public void 맴버권한수정(String userId, RoleType role){
        // 영속성 컨텍스트 내부에 있는 객체를 가져옴

        Member member = repository.findByUserId(userId);
        log.info("member : {}", member);
        // Validations
        validate(member);

        member.setRole(role);
        member.setEmail("change@test.com");
        // password null error https://java8.tistory.com/509

    }

    @Transactional(readOnly = true)
    public List<Member> 멤버목록(){
        List<RoleType> roleTypes = new ArrayList<>();
        roleTypes.add(RoleType.CORE);
        roleTypes.add(RoleType.LEAD);
        roleTypes.add(RoleType.MEMBER);
        return repository.findMembersByRoleInAndMemberInfo_PhoneNumberIsNotNull(roleTypes);
    }

    @Transactional(readOnly = true)
    public List<Member> 전체회원목록(){
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Member> 게스트목록(){
        List<RoleType> roleTypes = new ArrayList<>();
        roleTypes.add(RoleType.GUEST);
        return repository.findMembersByRoleInAndMemberInfo_PhoneNumberIsNotNull(roleTypes);
    }

    @Transactional
    public void 경고주기(String fromUser, WarningDto warningDto) {
        Member admin = repository.findByUserId(fromUser);
        Member ToUser = repository.findByUserId(warningDto.getToUser());
        WarnDescription warnDescription = WarnDescription.builder()
                        .title(warningDto.getTitle())
                        .fromUser(admin)
                        .toUser(ToUser)
                        .content(warningDto.getContent())
                        .build();
        jpaWarnDescription.save(warnDescription);
    }
    // 유효성 검사
    private void validate(final Member member){
        if(member == null){
            log.warn("Domain cannot be null.");
            throw new RuntimeException("Domain cannot be null");
        }

        if(member.getUsername() == null){
            log.warn("없는 사용자 입니다.");
            throw new RuntimeException("없는 사용자 입니다.");
        }
    }
}

