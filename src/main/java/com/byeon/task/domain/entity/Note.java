package com.byeon.task.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sendMessage;
    private String translateMessage;

    @ManyToOne(fetch = LAZY)
    private Member member;


    public void addMember(Member member) {
        this.member = member;
        member.getNotes().add(this);
    }
}
