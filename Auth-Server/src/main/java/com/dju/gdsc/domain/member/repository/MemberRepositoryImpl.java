package com.dju.gdsc.domain.member.repository;

import com.dju.gdsc.domain.member.entity.Member;
import com.dju.gdsc.domain.member.entity.QMember;
import com.dju.gdsc.domain.member.entity.QMemberInfo;
import com.dju.gdsc.domain.member.entity.QSlackMemberInfo;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Repository
public class MemberRepositoryImpl implements CustomMemberRepository {

    private final EntityManager em;

    @Autowired
    public MemberRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    // 이 아래 작성
    public Member findByUserId(String id) {
        // querydsl 사용
        JPAQueryFactory query = new JPAQueryFactory(em);

        QMember m = new QMember("m");
        QSlackMemberInfo s = new QSlackMemberInfo("s");
        Tuple t = query.select(m, s)
                .from(m)
                .leftJoin(s)
                .on(m.eq(s.userId)).where(m.userId.eq(id)).fetchOne();

        if (Objects.isNull(t)) {
            throw new IllegalArgumentException("해당 유저가 존재하지 않습니다.");
        }
        Member returnMember =
                Member.builder()
                .userId(t.get(m).getUserId())
                .email(t.get(m).getEmail())
                .memberInfo(t.get(m).getMemberInfo())
                .emailVerifiedYn(t.get(m).getEmailVerifiedYn())
                .modifiedAt(t.get(m).getModifiedAt())
                .profileImageUrl(t.get(m).getProfileImageUrl())
                .providerType(t.get(m).getProviderType())
                .role(t.get(m).getRole())
                .uploadDate(t.get(m).getUploadDate())
                .username(t.get(m).getUsername())
                .build();
        if(t.get(s) != null) {
            returnMember.setProfileImageUrl(Objects.requireNonNull(t.get(s)).getProfileImage512());
        }
        return returnMember;

    }

    public List<Member> findAll() {
        JPAQueryFactory query = new JPAQueryFactory(em);

        QMember m = new QMember("m");
        QSlackMemberInfo s = new QSlackMemberInfo("s");
        List<Tuple> t = query.select(m, s)
                .from(m)
                .leftJoin(s)
                .on(m.eq(s.userId)).fetch();

        return null;
    }
}
