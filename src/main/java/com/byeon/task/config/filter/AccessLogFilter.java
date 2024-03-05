package com.byeon.task.config.filter;

import com.byeon.task.common.CommonMessage;
import com.byeon.task.dto.AccessLogDto;
import com.byeon.task.dto.AccessLogMQDto;
import com.byeon.task.repository.AccessLogRepository;
import com.byeon.task.repository.ConfigRepository;
import com.byeon.task.consumers.MessageService;
import com.byeon.task.service.ConfigService;
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
    private final ConfigService configService;

    private final CommonMessage messageSource;

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
            String clientRequest = getRequest(wrapperRequest);
            log.info("clientRequest = {}", clientRequest);


            // 여기에서 wrapperResponse responseBody를 꺼내서 AccessLog 에 추가.
            String clientResponse = getResponse(wrapperResponse);
            log.info("clientResponse = {}", clientResponse);

            // 여기에 로그인된 유저가 사용한 api 라면 userId 를 한번 추가해보세요. 힌트는 쓰레드로컬 입니다.
            // 여기에서 위에서 측정한 시간을 AccessLog 에 추가.
            // 그냥 insert 말고 여기서 RabbitMQ로 큐에 10개 저장되면 insert 되게 변경
            AccessLogMQDto accessLogMQDto = createAccessLogMQDto(accessLogDto, clientRequest, clientResponse, elapseTime);
            // AccessLog savedLog = accessLogRepository.save(accessLog);
            messageService.sendMQAccessLog(accessLogMQDto);

            // 만약 elapsedTime 이 {}초 이상 넘어가면 텔레그램으로 noti 를 주는건 어떨까요?
            isTimeOut(elapseTime, accessLogDto.getUri()); // 10초 이상이면 텔레그램 noti

            wrapperResponse.copyBodyToResponse();
        }finally {
            threadLocalSaveUserID.removeStoredUserId();
        }
    }

    /**
     * 1. 캐시를 저장하고 찾고 로직이 필요한가?
     *  --> 답: 아니오. ===> 스프링 캐시 추상화 라는 기술이 해줍니다.
     *  --> AOP 라는 기술이 이를 가능하게 합니다.
     *
     * 2. 캐시설정
     *  --> 1. 인메모리 (분산X, WAS별로 다르겟죠. 그래도 되면 써도 됩니다.)
     *  --> 2. Redis (분산O) --> O(1) 언제나. --> 메모리라서, 날라가죠. 디폴트로 1분당 한번씩 메모리 to Disk 저장.
     *    : 20000 TPS
     *    : HA: 클러스터링 고려하거나 샤딩하거냐, 둘다 하거나
     */
    public void isTimeOut(double elapseTime, String uri) {
        //  10 이면 10초가 아닌것 같습니다 :) 업데이트 필요합니다.
        // todo 이런 값처럼 처음에 000 인줄알았다가 runtime 값을 변경하게 되는 경우가 있을텐데요. 굳이 배포하지 않고 어떻게 하면 쉽게 변경할 수 있을까요? 한번 생각해보시고 실행해보시면 좋을 것 같아요.
        // DB 에 저장되어 있는 Timeout 시간 조회 -> sql 로 직접 넣기

        // todo ConfigService 를 만들고요. 거기에서 @Cacheable 을 활용해봤으면 합니다. 기본적으로 인메모리 구조로 세팅해보시고 한번 시도해보시죠..
        String confValue = configService.getConfElapseTime("confElapseTime");
        log.info("confValue = {}", confValue);
        double confElapseTime = Double.parseDouble(confValue);
        log.info("confElapseTime = {}", confElapseTime);

        // messages.properties 값에 의해 변경,
        //  messageSource 를 한번더 wrapping 하여 클래스를 만들어 사용하는게 어떨까요 ? 파라미터에 null, Local.getDefault() 를 넣는것보다는 좀더 낫지 않을까 해서요. getTimeoutForElapsedTime() 이정도로 하면 좋지 않을까 싶네요.
//        String confElapseTimeStr = messageSource.getTimeoutForElapsedTime();
//        double confElapseTime = Double.parseDouble(confElapseTimeStr);x
        log.info("elapseTime = {}", elapseTime);
        if (elapseTime >= confElapseTime) {

            //  좀더 구체적인 메시지가 있어야 되지 않을까 해요. 메시지만 보더라도 어떤 API 에서 문제가 발생했고 얼마나 걸렸는지를 알면 좋을 것 같습니다.
            telegramService.sendMessage(HttpStatus.REQUEST_TIMEOUT, System.lineSeparator() + "uri : " + uri + System.lineSeparator() + "걸린시간 : " + elapseTime + " 초" + System.lineSeparator() + "해당 로직 수정이 필요합니다!");
        }
    }

    //  여기 메소드 이름에 Cashing 이 들어가 있는게 조금 이해가 안되네요. 앞으로 다루게 될 캐시와 오버랩 되면서 햇갈릴 것 같아서 리네이밍을 해주시면 좋을것같습니다.
    private static String getResponse(ContentCachingResponseWrapper wrapperResponse) {
        byte[] contentResponseAsArray = wrapperResponse.getContentAsByteArray();
        String cashingResponse = new String(contentResponseAsArray, StandardCharsets.UTF_8);
        return cashingResponse;
    }

    // 여기 메소드 이름에 Cashing 이 들어가 있는게 조금 이해가 안되네요. 앞으로 다루게 될 캐시와 오버랩 되면서 햇갈릴 것 같아서 리네이밍을 해주시면 좋을것같습니다.
    private static String getRequest(ContentCachingRequestWrapper wrapperRequest) {
        byte[] contentRequestAsArray = wrapperRequest.getContentAsByteArray();
        return new String(contentRequestAsArray, StandardCharsets.UTF_8);
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
}
