package com.dju.gdsc.domain.member.repository;



import com.dju.gdsc.domain.member.entity.Member;
import com.dju.gdsc.domain.member.entity.MemberInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaMemberInfoRepository extends JpaRepository<MemberInfo, Integer> {
    Optional<MemberInfo> findByMember(Member member);
}
