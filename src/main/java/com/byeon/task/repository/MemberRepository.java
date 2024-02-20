package com.byeon.task.repository;

import com.byeon.task.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    public Optional<Member> findMemberByUserId(String userId);
}
