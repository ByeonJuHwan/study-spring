package com.byeon.task.domain;

import com.byeon.task.dto.MemberJoinDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private String password;


    @Builder
    public Member(Long id, String userId, String password) {
        this.id = id;
        this.userId = userId;
        this.password = password;
    }

    public static Member toEntity(MemberJoinDto dto) {
        return Member.builder()
                .userId(dto.getUserId())
                .password(dto.getPassword())
                .build();
    }

}
