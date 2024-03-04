package com.byeon.task.service;

import com.byeon.task.domain.entity.Member;
import com.byeon.task.domain.entity.Note;
import com.byeon.task.dto.NoteCreateDto;
import com.byeon.task.dto.NoteSearchDto;
import com.byeon.task.repository.MemberRepository;
import com.byeon.task.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final MemberRepository memberRepository;

    public void saveVocalNote(NoteCreateDto noteCreateDto, Member member) {
        Note note = Note.builder()
                .sendMessage(noteCreateDto.getSendMessage())
                .translateMessage(noteCreateDto.getTranslateMessage())
                .build();

        note.addMember(member);
        noteRepository.save(note);
    }

    public List<Note> findMyNotes() {
        //  여기서도 한번씩 멤버 검색을 또해야함
        if (isLoginUser()) {
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            Member member = memberRepository.findMemberByUserId(userId).get();
            return noteRepository.findAllByMember(member);
        }
        return Collections.emptyList();
    }

    private Boolean isLoginUser() {
        return SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
    }
}
