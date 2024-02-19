package com.byeon.task.service;

import com.byeon.task.domain.Member;
import com.byeon.task.dto.MemberJoinDto;
import com.byeon.task.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public void join(MemberJoinDto memberJoinDto) {
        Member savedMember = memberRepository.save(Member.toEntity(memberJoinDto));
        log.info("savedMember = {}", savedMember);
    }
}
