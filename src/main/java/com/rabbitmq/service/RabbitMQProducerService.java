package com.rabbitmq.service;

import com.rabbitmq.model.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQProducerService {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQProducerService.class);
    private final AmqpTemplate rabbitTemplate;

    private static final String EXCHANGE = "exchange1";
    private static final String STR_QUEUE = "queueStr";
    private static final String JSON_QUEUE="queueJSON";
    private static final String ROUTE_KEY_FOR_STR_QUEUE = "route_queueStr";
    private static final String ROUTE_KEY_FOR_JSON_QUEUE = "route_queueJSON";



    @Autowired
    public RabbitMQProducerService(AmqpTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public String sendStringMessage(String message) {
        //Todo: rabbitmq client.

        try {

            logger.info(String.format("RabbitMQProducerService - sendingMessage | '%s' string message is sending...", message));
            rabbitTemplate.convertAndSend(EXCHANGE, ROUTE_KEY_FOR_STR_QUEUE, message);

            return "OK";

        } catch (Exception e) {

            e.printStackTrace();
            return "NOK";
        }


    }

    public String sendJsonMessage(UserDto dto) {
        try {
            logger.info(
                    String.format(
                            "RabbitMQProducerService - sendingJsonMessage | '%s' string message is sending...",
                            dto
                    )
            );

            rabbitTemplate.convertAndSend(
                    EXCHANGE,
                    ROUTE_KEY_FOR_JSON_QUEUE,
                    dto
            );

            return "OK";

        } catch (Exception e) {
            e.printStackTrace();
            return "NOK";
        }
    }

}
