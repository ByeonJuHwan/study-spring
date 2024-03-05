package com.byeon.task.consumers;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

//    @Value("${spring.rabbitmq.host}")
//    private String rabbitmqHost;
//
//    @Value("${spring.rabbitmq.port}")
//    private int rabbitmqPort;

//    @Value("${spring.rabbitmq.username}")
//    private String rabbitmqUsername;
//
//    @Value("${spring.rabbitmq.password}")
//    private String rabbitmqPassword;

    /**
     * direct 설정
     */
    public static final String TELEGRAM_QUEUE = "telegram";
    public static final String TELEGRAM_EXCHANGE = "telegram.exchange";
    public static final String TELEGRAM_ROUTE_KEY = "telegram.route.key";

    /**
     * fan out 설정
     */

    public static final String ACCESS_LOG_QUEUE = "access.log";
    public static final String VOCAL_QUEUE = "voca";
    public static final String FANOUT_EXCHANGE = "access.voca.exchange";

    /**
     * DeadQueue 설정
     */

    public static final String DEAD_QUEUE = "dead";
    public static final String DEAD_EXCHANGE = "dead.exchange";
    public static final String DEAD_ROUTE_KEY = "dead.key";

    /**
     * rabbitMQ 큐
     */

    @Bean
    public Queue accessLogQueue() {
        return new Queue(ACCESS_LOG_QUEUE);
    }
    @Bean
    public Queue vocalQueue() {
        return new Queue(VOCAL_QUEUE);
    }

    @Bean
    public Queue telegramQueue() {
        return new Queue(TELEGRAM_QUEUE);
    }

    @Bean
    public Queue deadQueue() {
        return new Queue(DEAD_QUEUE);
    }

    /**
     * rabbitMQ 익스체인지
     */
    /*
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchangeName);
    }*/
    @Bean
    public DirectExchange telegramExchange() {
        return new DirectExchange(TELEGRAM_EXCHANGE);
    }

    @Bean
    public DirectExchange deadExchange() {
        return new DirectExchange(DEAD_EXCHANGE);
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    /**
     * rabbitMQ 의 큐랑 익스체인지를 라우팅 키로 바인딩
     */
    /*@Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }*/

    @Bean
    Binding bindingAccessLog(Queue accessLogQueue, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(accessLogQueue).to(fanoutExchange);
    }

    @Bean
    Binding bindingVocal(Queue vocalQueue, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(vocalQueue).to(fanoutExchange);
    }

    @Bean
    public Binding bindingTelegram(Queue telegramQueue,DirectExchange telegramExchange) {
        return BindingBuilder.bind(telegramQueue).to(telegramExchange).with(TELEGRAM_ROUTE_KEY);
    }

    @Bean
    public Binding bindingDeadQueue(Queue deadQueue, DirectExchange deadExchange) {
        return BindingBuilder.bind(deadQueue).to(deadExchange).with(DEAD_ROUTE_KEY);
    }
    /**
     * rabbitMQ 템플릿 설정 dto 직렬화 역직렬화
     */

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory, MessageConverter jackson2JsonMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jackson2JsonMessageConverter);
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        // JSON 형식의 메시지를 직렬화하고 역직렬할 수 있도록 설정
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}


