package com.dju.gdsc.domain.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
public class SlackMemberInfo {
    @Id
    private String slackUserId;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private Member userId;
    private String name;
    private String slackDisplayName;
    private String profileImage72;
    private String profileImage512;
}
