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

    @Transactional
    @RabbitListener(queues = "${rabbitmq.queue.name}", containerFactory = "rabbitListenerContainerFactory")
    public void receiveMessage(List<AccessLogMQDto> accessLogs) {
        log.error("size = {}", accessLogs.size()); // 1밖에 안나오는 이유..?
        List<AccessLog> accessLogList = new ArrayList<>();
        for (AccessLogMQDto accessLog : accessLogs) {
            log.info("Received access log: {}", accessLog);
            accessLogList.add(createAccessLog(accessLog));
        }
        repository.saveAll(accessLogList);
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