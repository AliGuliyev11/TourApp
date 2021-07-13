package com.mycode.tourapptelegrambot.rabbitmq.rabbitmqconfig;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
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
    Binding bind(Queue queue,DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("orderKey");
    }

    @Bean
    MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

}
