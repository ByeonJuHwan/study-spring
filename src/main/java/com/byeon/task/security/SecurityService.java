package com.byeon.task.security;

import com.byeon.task.domain.entity.Member;
import com.byeon.task.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class SecurityService implements UserDetailsService {

    private final MemberRepository memberRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findMemberByUserId(username)
                .orElseThrow(() -> new UsernameNotFoundException("아이디가 없습니다 : " + username));

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");

        return new User(member.getUserId(), member.getPassword(), Collections.singletonList(authority));
    }
}
