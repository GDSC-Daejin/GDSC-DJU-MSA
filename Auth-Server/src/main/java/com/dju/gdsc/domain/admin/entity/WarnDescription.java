package com.dju.gdsc.domain.admin.entity;

import com.dju.gdsc.domain.member.entity.Member;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "id")
public class WarnDescription {
    @Id
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Schema(description = "제목" , example = "제목")
    String title;
    @Lob
    @Schema(description = "내용" , example = "내용")
    String content;

    @JoinColumn(name = "FROM_USER_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    @Schema(description = "작성자" , example = "누구")
    private Member fromUser;
    @ManyToOne(fetch = FetchType.LAZY)
    @Schema(description = "작성자" , example = "누구")
    @JoinColumn(name = "TO_USER_ID")
    private Member toUser;



    @CreationTimestamp
    @Schema(description = "작성일" , example = "2022-01-06 14:57:42.777000")
    private LocalDateTime uploadDate;
}
