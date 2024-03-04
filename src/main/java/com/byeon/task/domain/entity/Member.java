package com.byeon.task.domain.entity;

import com.byeon.task.dto.MemberJoinDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@ToString
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private String password;

    @OneToMany(mappedBy = "member")
    private List<Note> notes = new ArrayList<>();


    @Builder
    public Member(Long id, String userId, String password, List<Note> notes) {
        this.id = id;
        this.userId = userId;
        this.password = password;
        this.notes = notes;
    }

    public static Member toEntity(MemberJoinDto dto) {
        return Member.builder()
                .userId(dto.getUserId())
                .password(dto.getPassword())
                .build();
    }

}
