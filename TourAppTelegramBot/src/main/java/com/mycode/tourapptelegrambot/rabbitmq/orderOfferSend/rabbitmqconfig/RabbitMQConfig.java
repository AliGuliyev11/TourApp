package com.mycode.tourapptelegrambot.rabbitmq.orderOfferSend.rabbitmqconfig;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    Queue rabbitQueue(){
        Queue orderQueue = new Queue("orderQueue", true);
        return orderQueue;
    }

    @Bean
    DirectExchange rabbitExchange(){
        return new DirectExchange("orderExchange");
    }

    @Bean
    Binding bind(@Qualifier("rabbitQueue") Queue queue, @Qualifier("rabbitExchange") DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("orderKey");
    }

    @Bean
    MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    Queue stopRabbitQueue(){
        Queue orderQueue = new Queue("stopOrderQueue", true);
        return orderQueue;
    }

    @Bean
    DirectExchange stopRabbitExchange(){
        return new DirectExchange("stopOrderExchange");
    }

    @Bean
    Binding stopBind(@Qualifier("stopRabbitQueue") Queue queue, @Qualifier("stopRabbitExchange") DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("stopOrderKey");
    }


    @Bean
    Queue offerReplyQueue(){
        Queue orderQueue = new Queue("offerReplyQueue", true);
        return orderQueue;
    }

    @Bean
    DirectExchange offerReplyRabbitExchange(){
        return new DirectExchange("offerReplyExchange");
    }

    @Bean
    Binding offerReplyBind(@Qualifier("offerReplyQueue") Queue queue, @Qualifier("offerReplyRabbitExchange") DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("offerReplyKey");
    }

}
