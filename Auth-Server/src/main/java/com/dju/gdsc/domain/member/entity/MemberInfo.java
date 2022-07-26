package com.dju.gdsc.domain.member.entity;


import com.dju.gdsc.domain.member.dto.MemberInfoResponseDto;
import com.dju.gdsc.domain.member.model.PositionType;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@RequiredArgsConstructor
@Builder
@Entity
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "id")
public class MemberInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_INFO_ID")
    private int memberInfoId;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "USER_ID")
    @JsonIgnore
    private Member member;

    @Column(name = "GDSC_GENERATION")
    @Schema(description = "유저의 기수" , example = "1")
    private Integer generation;
    @Column
    @Schema(description = "소개" , example = "안녕하세요")
    private String introduce;

    @Column(length = 30 ,unique = true)
    @Schema(description = "이름" , example = "Rocoli")
    private String nickname;

    @Column(length = 30)
    @Schema(description = "전화번호" , example = "010-9132-1234")
    private String phoneNumber;

    @Column
    @Schema(description = "학과" , example = "산업경영공학과")
    private String major;

    @Column(name = "GIT_EMAIL" , length = 30)
    @Schema(description = "GitHub 이메일" , example = "gudcks0305")
    private String gitEmail;

    @Column(length = 30)
    @Schema(description = "아이디" , example = "20177878")
    private String studentID;

    @Column(name = "POSITION_TYPE")
    @Schema(description = "직책" , example = "Backend")
    private PositionType PositionType;

    @Column(name = "HashTag")
    @Schema(description = "해시태그" , example = "#안녕하세요")
    private String hashTag;

    /*/// not table 속성
    @OneToMany(mappedBy = "memberInfo")
    private List<MemberScrapPost> memberScrapPostList;

    @OneToMany(mappedBy = "memberInfo" , cascade = CascadeType.REMOVE)
    private List<Post> myPost;
*/  @Column(name = "GIT_URL")
    private String gitHubUrl;
    @Column(name = "BLOG_URL")
    private String blogUrl;
    @Column(name = "ETC_URL")
    private String etcUrl;


    @Schema(description = "회원 가입일" , example = "1998-07-09 00:00:00.000000")
    private Date birthday;

    @Column(name = "MODIFIED_AT")
    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @CreationTimestamp
    @Schema(description = "회원 가입일" , example = "1998-07-09 00:00:00.000000")
    private LocalDateTime uploadDate;


    public MemberInfoResponseDto toMemberInfoResponseDto() {
        return new MemberInfoResponseDto(nickname , member.toResponseDto());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        MemberInfo that = (MemberInfo) o;
        return  Objects.equals(memberInfoId, that.memberInfoId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    @Override
    public String toString(){
        return "MemberInfo{" +
                "memberInfoId=" + memberInfoId +
                "\n member=" + member +
                "\n generation=" + generation +
                "\n introduce='" + introduce + '\'' +
                "\n nickname='" + nickname + '\'' +
                "\n phoneNumber='" + phoneNumber + '\'' +
                "\n major='" + major + '\'' +
                "\n gitEmail='" + gitEmail + '\'' +
                "\n StudentID='" + studentID + '\'' +
                "\n PositionType=" + PositionType +
                "\n hashTag='" + hashTag + '\'' +
                "\n gitHubUrl='" + gitHubUrl + '\'' +
                "\n blogUrl='" + blogUrl + '\'' +
                "\n etcUrl='" + etcUrl + '\'' +
                "\n birthday=" + birthday +
                "\n modifiedAt=" + modifiedAt +
                "\n uploadDate=" + uploadDate +
                '}';
    }
}
