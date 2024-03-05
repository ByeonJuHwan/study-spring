package com.byeon.task.consumers;

import com.byeon.task.dto.AccessLogMQDto;
import com.byeon.task.dto.TelegramDeadMessageDto;
import com.byeon.task.service.TelegramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramConsumer {

    private final TelegramService telegramService;
    private final MessageService messageService;

    @Transactional
    @RabbitListener(queues = RabbitMQConfig.TELEGRAM_QUEUE, concurrency = "1", containerFactory = "rabbitListenerContainerFactory")// todo concurrency 라는 속성이 있습니다. 쓰레드생성수를 컨트롤 할 수 있습니다. 싱글쓰레드로 처리할수있도록 해보세요.
    public void receiveMessage(TelegramDeadMessageDto deadMessageDto) {
        try {
            String requestInfo = String.format("Method: %s, Request URI: %s", deadMessageDto.getRequestMethod(), deadMessageDto.getUri());
            String errorMessage = String.format("서버 오류 발생 - %s - 에러 메세지 : %s", requestInfo, deadMessageDto.getErrorMessage());
            telegramService.sendMessage(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
            //throw new Exception();
        } catch (Exception e) {
            // 여기는 데드큐 전략
            messageService.sendDeadQueue(deadMessageDto);
        }
    }
}
