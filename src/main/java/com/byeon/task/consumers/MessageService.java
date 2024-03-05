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
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 저는 MessageService 와 AccessLogConsumer, VocalConsumer 를 따로 분리하는게 좋아 보입니다.
 * 컨슈머 단을 아예 별도로 분리할수도 있기 때문에 애초부터 따로 노는게 좋을 것 같아요.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final RabbitTemplate rabbitTemplate;

    // todo 여기도 @Async 게 있으면 좀더 비동기적으로 해볼수있겠지요. 물론 너무 @Async 를 남발하게 되면 별도의 쓰레드풀도 힘들어할수도 있습니다. 그러면 배보다 배꼽이 더 커질수는 있어서요.
    @Async
    public void sendMQAccessLog(AccessLogMQDto accessLog) {
        log.info("sendMQ = {}", accessLog);
        rabbitTemplate.convertAndSend(RabbitMQConfig.FANOUT_EXCHANGE, "", accessLog);
    }
}