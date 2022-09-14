package com.dju.gdsc.domain.member.repository;


import com.dju.gdsc.domain.member.entity.Member;
import com.dju.gdsc.domain.member.model.RoleType;
import com.dju.gdsc.domain.oauth.entity.ProviderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer>, CustomMemberRepository {
    //Optional<Member> findByEmail(String email);
    // Optional<Member> findByUsername(String username);

    List<Member> findMembersByRoleInAndMemberInfo_PhoneNumberIsNotNull(@Param("role") List<RoleType> roleTypes);
    Member findByEmail(String email);
    Optional<Member> findByProviderTypeAndUserId(ProviderType providerType, String userId);

    boolean existsByMemberInfo_Nickname(String updateNickname);


    Optional<Member> findByMemberInfo_Nickname(String nickname);
}
