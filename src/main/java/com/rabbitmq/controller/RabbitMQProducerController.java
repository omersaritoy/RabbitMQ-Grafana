package com.rabbitmq.controller;


import com.rabbitmq.model.UserDto;
import com.rabbitmq.service.RabbitMQProducerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RabbitMQProducerController {
    private final RabbitMQProducerService rabbitMQProducerService;

    public RabbitMQProducerController(RabbitMQProducerService rabbitMQProducerService) {
        this.rabbitMQProducerService = rabbitMQProducerService;
    }

    @PostMapping("/sendStringMessage")
    public String sendStringMessage(@RequestBody String message){
        return rabbitMQProducerService.sendStringMessage(message);
    }
    @PostMapping("/sendJsonMessage")
    public String sendJsonMessage(@RequestBody UserDto dto) {
        return rabbitMQProducerService.sendJsonMessage(dto);
    }
}
