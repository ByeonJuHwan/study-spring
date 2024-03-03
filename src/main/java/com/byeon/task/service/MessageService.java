package com.byeon.task.service;

import com.byeon.task.domain.entity.AccessLog;
import com.byeon.task.dto.AccessLogDto;
import com.byeon.task.dto.AccessLogMQDto;
import com.byeon.task.repository.AccessLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;
    private final AccessLogRepository repository;

    public void sendMQAccessLog(AccessLogMQDto accessLog) {
        log.info("sendMQ = {}", accessLog);
        rabbitTemplate.convertAndSend(exchangeName, routingKey, accessLog);
    }

    // fixme 네 여기서는 메시지를 1개만 받고 여기에서 바로 엑세스로그를 저장해야합니다. 물론 던지는 쪽에서도 1개씩만 전달하게 되겠지요.
    // fixme 여기에서 예를 들어 10개씩 모아서 저장할 수도 있겠지요. 우선 하나씩 저장하는것은 맞습니다.
    @Transactional
    @RabbitListener(queues = "${rabbitmq.queue.name}", containerFactory = "rabbitListenerContainerFactory")     // todo concurrency 라는 속성이 있습니다. 쓰레드생성수를 컨트롤 할 수 있습니다. 싱글쓰레드로 처리할수있도록 해보세요.
    public void receiveMessage(List<AccessLogMQDto> accessLogs) {
        try {
            List<AccessLog> accessLogList = new ArrayList<>();
            for (AccessLogMQDto accessLog : accessLogs) {
                log.info("Received access log: {}", accessLog);
                accessLogList.add(createAccessLog(accessLog));
            }
            repository.saveAll(accessLogList);
        }catch (Exception e) {
            // dead queue 로 ... or 아니면 다른 전략이 필요.
            // Telegram 으로 보내기도 하고...
            // Telegram 보내는 컨슈머한테 보내달라고 한다. (MSA)
            //   - HTTP X
            //   - MQ 로 연결. (비동기연결)
        }
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