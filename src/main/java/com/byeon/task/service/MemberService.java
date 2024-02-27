package com.byeon.task.service;

import com.byeon.task.domain.entity.Member;
import com.byeon.task.dto.MemberJoinDto;
import com.byeon.task.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public void join(MemberJoinDto memberJoinDto) {
        memberJoinDto.setPassword(passwordEncoder.encode(memberJoinDto.getPassword())); // password 인코딩
        // modelMapper > entity -> dto
        Member savedMember = memberRepository.save(modelMapper.map(memberJoinDto, Member.class));
        log.info("savedMember = {}", savedMember);
    }

    public List<Member> getAll() {
        return memberRepository.findAll();
    }
}
