package com.mycode.tourapptelegrambot.rabbitmq.orderOfferSend.rabbitservice;

import com.mycode.tourapptelegrambot.dto.ReplyToOffer;
import com.mycode.tourapptelegrambot.models.Order;
import com.mycode.tourapptelegrambot.rabbitmq.orderOfferSend.rabbitmqconfig.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service(value = "sendOrder")
public class RabbitMQService {
    private final RabbitTemplate template;

    public RabbitMQService(RabbitTemplate template) {
        this.template = template;
    }

    public void send(Order order){
        template.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE,RabbitMQConfig.ORDER_KEY,order);
    }
    public void stop(String uuid){
        template.convertAndSend(RabbitMQConfig.STOP_ORDER_EXCHANGE,RabbitMQConfig.STOP_ORDER_KEY,uuid);
    }
    public void reply(ReplyToOffer reply){
        template.convertAndSend(RabbitMQConfig.OFFER_REPLY_EXCHANGE,RabbitMQConfig.OFFER_REPLY_KEY,reply);
    }
}
