package com.dju.gdsc.domain.member.repository;

import com.dju.gdsc.domain.member.entity.Member;
import com.dju.gdsc.domain.member.entity.QMember;
import com.dju.gdsc.domain.member.entity.QMemberInfo;
import com.dju.gdsc.domain.member.entity.QSlackMemberInfo;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.Collections;
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
    // read only 따라서 트랜잭션 필요 없음
    // 따라서 영속성 컨텍스트에 저장되지 않음
    // 그렇기 때문에 해당 메소드를 사용하는 경우 set 값을 통해 데이터 베이스에 저장되지 않음
    public Member findByUserIdWithSlack(String id) {
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
        return createMemberDto(m, s, t);
    }

    @Override
    public List<Member> findMembersWithSlack() {
        JPAQueryFactory query = new JPAQueryFactory(em);

        QMember m = new QMember("m");
        QSlackMemberInfo s = new QSlackMemberInfo("s");
        List<Tuple> t = query.select(m, s)
                .from(m)
                .leftJoin(s)
                .on(m.eq(s.userId)).fetch();
        List<Member> returnMembers = new java.util.ArrayList<>(Collections.emptyList());
        t.forEach(tuple -> {
            Member returnMember  = createMemberDto(m, s, tuple);
            returnMembers.add(returnMember);
        });
        return returnMembers;

    }

    private Member createMemberDto(QMember m, QSlackMemberInfo s, Tuple tuple) {
        Member returnMember =
                Member.builder()
                        .userId(tuple.get(m).getUserId())
                        .email(tuple.get(m).getEmail())
                        .memberInfo(tuple.get(m).getMemberInfo())
                        .emailVerifiedYn(tuple.get(m).getEmailVerifiedYn())
                        .modifiedAt(tuple.get(m).getModifiedAt())
                        .profileImageUrl(tuple.get(m).getProfileImageUrl())
                        .providerType(tuple.get(m).getProviderType())
                        .role(tuple.get(m).getRole())
                        .uploadDate(tuple.get(m).getUploadDate())
                        .username(tuple.get(m).getUsername())
                        .build();
        if(tuple.get(s) != null) {
            returnMember.setProfileImageUrl(Objects.requireNonNull(tuple.get(s)).getProfileImage512());
        }
        return returnMember;
    }

}
