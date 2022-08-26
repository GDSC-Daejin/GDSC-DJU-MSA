package com.dju.gdsc.domain.member.repository;

import com.dju.gdsc.domain.member.entity.Member;
import com.dju.gdsc.domain.member.entity.SlackMemberInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlackMemberInfoRepository extends JpaRepository<SlackMemberInfo, String> {
    SlackMemberInfo findByUserId(Member userId);

}
