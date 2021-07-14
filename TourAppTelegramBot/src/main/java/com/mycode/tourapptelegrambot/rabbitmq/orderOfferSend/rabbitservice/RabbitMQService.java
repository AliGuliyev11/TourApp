package com.mycode.tourapptelegrambot.rabbitmq.orderOfferSend.rabbitservice;

import com.mycode.tourapptelegrambot.models.Order;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service(value = "sendOrder")
public class RabbitMQService {
    private final RabbitTemplate template;

    public RabbitMQService(RabbitTemplate template) {
        this.template = template;
    }

    public void send(Order order){
        template.convertAndSend("orderExchange","orderKey",order);
    }
    public void stop(String uuid){
        template.convertAndSend("orderExchange2","orderKey2",uuid);
    }
}
