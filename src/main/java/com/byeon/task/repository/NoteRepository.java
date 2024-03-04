package com.byeon.task.repository;

import com.byeon.task.domain.entity.Member;
import com.byeon.task.domain.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findAllByMember(Member member);
}

