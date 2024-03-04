package com.byeon.task.service.front;

import com.byeon.task.domain.entity.Note;
import com.byeon.task.dto.NoteSearchDto;
import com.byeon.task.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteFrontService {

    private final NoteService noteService;
    private final ModelMapper mapper;

    public List<NoteSearchDto> noteToSearchDto() {
        List<Note> notes = noteService.findMyNotes();
        return notes.stream().map(note -> mapper.map(note, NoteSearchDto.class)).toList();
    }
}
