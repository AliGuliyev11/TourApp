package com.mycode.tourapptelegrambot.rabbitmq.rabbitservice;

import com.mycode.tourapptelegrambot.models.Order;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQService {
    private final RabbitTemplate template;

    public RabbitMQService(RabbitTemplate template) {
        this.template = template;
    }

    public void send(Order order){
        template.convertAndSend("orderExchange","orderKey",order);
    }
}
