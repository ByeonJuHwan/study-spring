package com.byeon.task.config.filter;

import com.byeon.task.dto.AccessLogDto;
import com.byeon.task.dto.AccessLogMQDto;
import com.byeon.task.repository.AccessLogRepository;
import com.byeon.task.service.MessageService;
import com.byeon.task.service.TelegramService;
import com.byeon.task.service.threadlocal.ThreadLocalSaveUserID;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
public class AccessLogFilter implements Filter {

    private final AccessLogRepository accessLogRepository;
    private final ThreadLocalSaveUserID threadLocalSaveUserID;
    private final TelegramService telegramService;
    private final MessageService messageService;

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
        }finally {
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
//            AccessLog savedLog = accessLogRepository.save(accessLog);
//            log.info("savedLog = {}", savedLog);
            // 여기서도 트라이 캐치..?
            messageService.sendMQAccessLog(accessLogMQDto);

            // 만약 elapsedTime 이 {}초 이상 넘어가면 텔레그램으로 noti 를 주는건 어떨까요?
            isTimeOut(elapseTime); // 10초 이상이면 텔레그램 noti

            wrapperResponse.copyBodyToResponse();

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
        if (elapseTime > 10) {
            telegramService.sendMessage(HttpStatus.REQUEST_TIMEOUT, "시간 단축 필요!!");
        }
    }
}
