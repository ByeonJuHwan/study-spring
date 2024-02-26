package com.byeon.task.repository;

import com.byeon.task.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // fixme public 키워드는 디폴트로 들어 있기 때문에 삭제 해주세요.
    Optional<Member> findMemberByUserId(String userId);
}
