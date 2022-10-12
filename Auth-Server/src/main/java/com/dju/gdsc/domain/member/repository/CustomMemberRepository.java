package com.dju.gdsc.domain.member.repository;

import com.dju.gdsc.domain.member.entity.Member;

import java.util.List;

public interface CustomMemberRepository {
    Member findByUserIdWithSlack(String id);
    List<Member> findMembersWithSlack();
}
