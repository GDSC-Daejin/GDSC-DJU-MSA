package com.dju.gdsc.domain.member.repository;

import com.dju.gdsc.domain.member.entity.Member;

public interface CustomMemberRepository {
    Member findByUserId(String id);
}
