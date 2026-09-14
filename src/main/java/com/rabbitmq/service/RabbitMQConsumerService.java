package com.rabbitmq.service;

import com.rabbitmq.model.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQConsumerService {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQProducerService.class);
    private static final String EXCHANGE = "exchange1";
    private static final String STR_QUEUE = "queueStr";
    private static final String JSON_QUEUE="queueJSON";
    private static final String ROUTE_KEY_FOR_STR_QUEUE = "route_queueStr";
    private static final String ROUTE_KEY_FOR_JSON_QUEUE = "route_queueJSON";


    @RabbitListener(queues = {STR_QUEUE})
    public void consumeStringMessage(String message){
        logger.info("RabbitMQConsumerService - consumeStringMessage | '%s' string message has ben received",message);

    }
    @RabbitListener(queues = {JSON_QUEUE})
    public void consumeJSONMessage(UserDto dto){
        logger.info("RabbitMQConsumerService - consumeJSONMessage | '%s' UserDto message has ben received",dto);

    }

}
