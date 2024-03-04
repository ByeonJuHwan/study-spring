package com.byeon.task.consumers;

import com.byeon.task.domain.entity.AccessLog;
import com.byeon.task.domain.entity.Member;
import com.byeon.task.dto.AccessLogMQDto;
import com.byeon.task.dto.NoteCreateDto;
import com.byeon.task.repository.AccessLogRepository;
import com.byeon.task.repository.MemberRepository;
import com.byeon.task.service.NoteService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {
    /*
    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;*/

    private final RabbitTemplate rabbitTemplate;
    private final AccessLogRepository repository;
    private final MemberRepository memberRepository;
    private final NoteService noteService;

    public void sendMQAccessLog(AccessLogMQDto accessLog) {
        log.info("sendMQ = {}", accessLog);
        rabbitTemplate.convertAndSend(RabbitMQConfig.FANOUT_EXCHANGE, "", accessLog);
    }

    // fixme 네 여기서는 메시지를 1개만 받고 여기에서 바로 엑세스로그를 저장해야합니다. 물론 던지는 쪽에서도 1개씩만 전달하게 되겠지요.
    // fixme 여기에서 예를 들어 10개씩 모아서 저장할 수도 있겠지요. 우선 하나씩 저장하는것은 맞습니다.
    @Transactional
    @RabbitListener(queues = RabbitMQConfig.ACCESS_LOG_QUEUE, concurrency = "1", containerFactory = "rabbitListenerContainerFactory")// todo concurrency 라는 속성이 있습니다. 쓰레드생성수를 컨트롤 할 수 있습니다. 싱글쓰레드로 처리할수있도록 해보세요.
    public void receiveMessage(AccessLogMQDto accessLog) {
        try {
            repository.save(createAccessLog(accessLog));
        } catch (Exception e) {
            // dead queue 로 ... or 아니면 다른 전략이 필요.
            // Telegram 으로 보내기도 하고...
            // Telegram 보내는 컨슈머한테 보내달라고 한다. (MSA)
            //   - HTTP X
            //   - MQ 로 연결. (비동기연결)
        }
    }

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

    private AccessLog createAccessLog(AccessLogMQDto accessLogMQDto) {
        return AccessLog.builder()
                .ipAddress(accessLogMQDto.getIpAddress())
                .userAgent(accessLogMQDto.getUserAgent())
                .requestTime(accessLogMQDto.getRequestTime())
                .method(accessLogMQDto.getRequestMethod())
                .uri(accessLogMQDto.getUri())
                .requestBody(accessLogMQDto.getRequestBody())
                .responseBody(accessLogMQDto.getResponseBody())
                .elapseTime(accessLogMQDto.getElapseTime())
                .userId(accessLogMQDto.getUserId())
                .build();
    }
}