package com.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//message eğer bir producer ve consumer arasında bir kanal olur o yüzden channel denir.

@Configuration
public class RabbitMQConsumerConfig {

    private static final String EXCHANGE = "exchange1";
    private static final String STR_QUEUE = "queueStr";
    private static final String ROUTE_KEY_FOR_STR_QUEUE = "route_queueStr";
    private static final String JSON_QUEUE = "queueJSON";
    private static final String ROUTE_KEY_FOR_JSON_QUEUE = "route_queueJSON";




    @Bean
    public ConnectionFactory getConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");

        return connectionFactory;

    }
    @Bean
    public MessageConverter getJSONMessageConverter(){
        return new JacksonJsonMessageConverter();
    }
    @Bean
    public AmqpTemplate getCustomRabbitTemplate(){
        RabbitTemplate rabbitTemplate=new RabbitTemplate(getConnectionFactory());
        rabbitTemplate.setMessageConverter(getJSONMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public TopicExchange getExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue getStringQueue() {
        return new Queue(STR_QUEUE);
    }
    @Bean
    public Binding getBindingForStringQueue() {
        return BindingBuilder
                .bind(getStringQueue())
                .to(getExchange()).with(ROUTE_KEY_FOR_STR_QUEUE);
    }

    @Bean
    public Queue getJSONQueue() {
        return new Queue(JSON_QUEUE);
    }

    @Bean
    public Binding getBindingForJSONQueue() {
        return BindingBuilder
                .bind(getJSONQueue())
                .to(getExchange()).with(ROUTE_KEY_FOR_JSON_QUEUE);
    }
}
