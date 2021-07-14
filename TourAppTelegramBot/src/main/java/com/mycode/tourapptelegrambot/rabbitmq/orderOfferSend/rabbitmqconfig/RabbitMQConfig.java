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
    Queue rabbitQueue2(){
        Queue orderQueue = new Queue("orderQueue2", true);
        return orderQueue;
    }

    @Bean
    DirectExchange rabbitExchange2(){
        return new DirectExchange("orderExchange2");
    }

    @Bean
    Binding bind2(@Qualifier("rabbitQueue2") Queue queue, @Qualifier("rabbitExchange2") DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("orderKey2");
    }

    @Bean
    MessageConverter messageConverter2(){
        return new Jackson2JsonMessageConverter();
    }


}
