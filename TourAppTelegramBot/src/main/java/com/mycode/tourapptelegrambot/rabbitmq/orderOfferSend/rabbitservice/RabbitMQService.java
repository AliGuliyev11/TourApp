package com.mycode.tourapptelegrambot.rabbitmq.orderOfferSend.rabbitservice;

import com.mycode.tourapptelegrambot.dto.ReplyToOffer;
import com.mycode.tourapptelegrambot.rabbitmq.orderOfferSend.rabbitmqconfig.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Ali Guliyev
 * @version 1.0
 * @implNote publisher service class
 */


@Service(value = "sendOrder")
public class RabbitMQService {
    private final RabbitTemplate template;

    public RabbitMQService(RabbitTemplate template) {
        this.template = template;
    }

    public void send(Map<String,String> order){
        template.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE,RabbitMQConfig.ORDER_KEY,order);
    }
    public void stop(String uuid){
        template.convertAndSend(RabbitMQConfig.STOP_ORDER_EXCHANGE,RabbitMQConfig.STOP_ORDER_KEY,uuid);
    }
    public void reply(ReplyToOffer reply){
        template.convertAndSend(RabbitMQConfig.OFFER_REPLY_EXCHANGE,RabbitMQConfig.OFFER_REPLY_KEY,reply);
    }
}
