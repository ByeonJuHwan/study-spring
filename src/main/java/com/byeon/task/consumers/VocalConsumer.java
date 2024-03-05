package com.byeon.task.consumers;

import com.byeon.task.domain.entity.Member;
import com.byeon.task.dto.AccessLogMQDto;
import com.byeon.task.dto.NoteCreateDto;
import com.byeon.task.repository.MemberRepository;
import com.byeon.task.service.NoteService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class VocalConsumer {

    private final NoteService noteService;
    private final MemberRepository memberRepository;
    @Transactional
    @RabbitListener(queues = RabbitMQConfig.VOCAL_QUEUE, concurrency = "1", containerFactory = "rabbitListenerContainerFactory")
    public void insertVocNote(AccessLogMQDto accessLog) {
        try {
            if (!accessLog.getUserId().equals("anonymousUser")) {
                NoteCreateDto noteCreateDto = new NoteCreateDto();
                String requestBody = accessLog.getRequestBody();
                String responseBody = accessLog.getResponseBody();

                ObjectMapper mapper = new ObjectMapper();
                noteCreateDto.setTranslateMessage(getTranslateResult(mapper, responseBody));
                noteCreateDto.setSendMessage(getSentence(mapper, requestBody));

                // 계속 member 를 검색하는 로직이 돔 -> 로그인 정보를 어디 보관하고 가져다 쓰면 좋을거같음
                Member member = memberRepository.findMemberByUserId(accessLog.getUserId()).orElseThrow(() -> new RuntimeException("임시"));
                noteService.saveVocalNote(noteCreateDto,member);
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    private String getSentence(ObjectMapper mapper, String requestBody) throws JsonProcessingException {
        JsonNode Sentence = mapper.readTree(requestBody);
        return Sentence == null ? null : Sentence.path("text").asText();
    }

    private String getTranslateResult(ObjectMapper mapper, String responseBody) throws JsonProcessingException {
        JsonNode translate = mapper.readTree(responseBody);
        JsonNode translateResult = translate.path("translations");
        return translateResult == null ? null : translateResult.get(0).path("text").asText();
    }
}
