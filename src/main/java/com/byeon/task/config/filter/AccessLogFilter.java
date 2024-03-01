package com.byeon.task.config.filter;

import com.byeon.task.domain.entity.Config;
import com.byeon.task.dto.AccessLogDto;
import com.byeon.task.dto.AccessLogMQDto;
import com.byeon.task.repository.AccessLogRepository;
import com.byeon.task.repository.ConfigRepository;
import com.byeon.task.service.MessageService;
import com.byeon.task.service.TelegramService;
import com.byeon.task.service.threadlocal.ThreadLocalSaveUserID;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
public class AccessLogFilter implements Filter {

    private final AccessLogRepository accessLogRepository;
    private final ThreadLocalSaveUserID threadLocalSaveUserID;
    private final TelegramService telegramService;
    private final MessageService messageService;
    private final ConfigRepository configRepository;

    private final MessageSource messageSource;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        Instant startTime = null;
        Instant endTime;
        double elapseTime;

        AccessLogDto accessLogDto = createAccessLogDto(request);

        ContentCachingRequestWrapper wrapperRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrapperResponse = new ContentCachingResponseWrapper((HttpServletResponse) servletResponse);


        try{
            // 시간측정, 시작
            startTime = Instant.now();

            // 이 지점에서 예외가 발생하게 되면 ThreadLocal 에 있는 자원을 free 해줄수있는 기회가 없어집니다. 이부분을 패치해주시기 바랍니다.
            filterChain.doFilter(wrapperRequest, wrapperResponse);

            // 시간측정, 종료, 걸린시간 측정
            // 초단위 보다는 micro second 까지 기록해보시는게 좋을 것 같습니다. 그럼 DB 컬럼도 변경해주셔야 합니다. 시간관련된게 나와서 말씀드리면 datetime 과 timestamp 간의 차이를 파악해보시고
            endTime = Instant.now();
            elapseTime = ((double) Duration.between(startTime, endTime).toNanos() / 1000) / 1_000_000.0;

            //  여기에서 wrapperRequest requestBody 를 꺼내서 AccessLog 에 추가.
            byte[] contentRequestAsArray = wrapperRequest.getContentAsByteArray();
            String cashingRequest = new String(contentRequestAsArray, StandardCharsets.UTF_8);
            log.info("cashingRequest = {}", cashingRequest);


            // 여기에서 wrapperResponse responseBody를 꺼내서 AccessLog 에 추가.
            byte[] contentResponseAsArray = wrapperResponse.getContentAsByteArray();
            String cashingResponse = new String(contentResponseAsArray, StandardCharsets.UTF_8);
            log.info("cashingResponse = {}", cashingResponse);

            // 여기에 로그인된 유저가 사용한 api 라면 userId 를 한번 추가해보세요. 힌트는 쓰레드로컬 입니다.
            // 여기에서 위에서 측정한 시간을 AccessLog 에 추가.
            // 그냥 insert 말고 여기서 RabbitMQ로 큐에 10개 저장되면 insert 되게 변경
            AccessLogMQDto accessLogMQDto = createAccessLogMQDto(accessLogDto, cashingRequest, cashingResponse, elapseTime);
            // AccessLog savedLog = accessLogRepository.save(accessLog);
            messageService.sendMQAccessLog(accessLogMQDto);

            // 만약 elapsedTime 이 {}초 이상 넘어가면 텔레그램으로 noti 를 주는건 어떨까요?
            isTimeOut(elapseTime); // 10초 이상이면 텔레그램 noti

            wrapperResponse.copyBodyToResponse();
        }finally {
            threadLocalSaveUserID.removeStoredUserId();
        }
    }

    private AccessLogMQDto createAccessLogMQDto(AccessLogDto accessLogDto, String cashingRequest, String cashingResponse, double elapseTime) {
        return AccessLogMQDto.builder()
                .ipAddress(accessLogDto.getIpAddress())
                .userAgent(accessLogDto.getUserAgent())
                .requestTime(accessLogDto.getRequestTime())
                .requestMethod(accessLogDto.getRequestMethod())
                .uri(accessLogDto.getUri())
                .requestBody(cashingRequest)
                .responseBody(cashingResponse)
                .elapseTime(elapseTime)
                .userId(threadLocalSaveUserID.getUserId())
                .build();
    }

    private AccessLogDto createAccessLogDto(HttpServletRequest request) {
        return AccessLogDto.builder()
                .ipAddress(request.getRemoteAddr())
                .userAgent(request.getHeader("User-Agent"))
                .requestTime(LocalDateTime.now())
                .requestMethod(request.getMethod())
                .uri(request.getRequestURI())
                .build();
    }

    private void isTimeOut(double elapseTime) {
        // fixme 10 이면 10초가 아닌것 같습니다 :) 업데이트 필요합니다.
        // todo 이런 값처럼 처음에 000 인줄알았다가 runtime 값을 변경하게 되는 경우가 있을텐데요. 굳이 배포하지 않고 어떻게 하면 쉽게 변경할 수 있을까요? 한번 생각해보시고 실행해보시면 좋을 것 같아요.
        // DB 에 저장되어 있는 Timeout 시간 조회 -> sql 로 직접 넣기
//        Config config = configRepository.findConfigByConfigName("confElapseTime").orElseThrow(() -> new RuntimeException("설정값이 없습니다."));
//        log.info("config = {}", config);
//        double confElapseTime = Double.parseDouble(config.getConfigValue());

        // messages.properties 값에 의해 변경
        String confElapseTimeStr = messageSource.getMessage("timeout.elapseTime", null, Locale.getDefault());
        double confElapseTime = Double.parseDouble(confElapseTimeStr);
        log.info("confElapseTime = {}", confElapseTime);
        log.info("elapseTime = {}", elapseTime);
        if (elapseTime >= confElapseTime) {
            telegramService.sendMessage(HttpStatus.REQUEST_TIMEOUT, "시간 단축 필요!!");
        }
    }
}
