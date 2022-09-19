package com.dju.gdsc.domain.member.factory;

import com.dju.gdsc.domain.member.entity.Member;
import com.dju.gdsc.domain.member.entity.MemberInfo;
import com.dju.gdsc.domain.member.model.RoleType;
import com.dju.gdsc.domain.oauth.entity.ProviderType;

public class MemberEntityFactory {
    public static Member getMember(String userId) {
        Member m = Member.builder()
                .userId(userId)
                .email(userId + "@mail.com")
                .password("1234")
                .emailVerifiedYn("Y")
                .username(userId + "test")
                .role(RoleType.MEMBER)
                .providerType(ProviderType.GOOGLE)
                .profileImageUrl("test")
                .build();
        MemberInfo mi = MemberInfo.builder()
                .nickname(userId)
                .build();
        mi.setMember(m);
        m.setMemberInfo(mi);
        return m;
    }
}
