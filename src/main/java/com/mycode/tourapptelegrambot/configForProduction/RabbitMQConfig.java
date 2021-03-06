package com.mycode.tourapptelegrambot.configForProduction;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author Ali Guliyev
 * @version 1.0
 * @implNote publisher configuration class
 */

@Configuration
@Profile("dev")
public class RabbitMQConfig {


    public static final String ORDER_QUEUE="orderQueue";
    public static final String ORDER_EXCHANGE="orderExchange";
    public static final String ORDER_KEY="orderKey";

    public static final String STOP_ORDER_QUEUE="stopOrderQueue";
    public static final String STOP_ORDER_EXCHANGE="stopOrderExchange";
    public static final String STOP_ORDER_KEY="stopOrderKey";

    public static final String OFFER_REPLY_QUEUE="offerReplyQueue";
    public static final String OFFER_REPLY_EXCHANGE="offerReplyExchange";
    public static final String OFFER_REPLY_KEY="offerReplyKey";

    @Bean
    Queue rabbitQueue(){
        Queue orderQueue = new Queue(ORDER_QUEUE, true);
        return orderQueue;
    }

    @Bean
    DirectExchange rabbitExchange(){
        return new DirectExchange(ORDER_EXCHANGE);
    }

    @Bean
    Binding bind(@Qualifier("rabbitQueue") Queue queue, @Qualifier("rabbitExchange") DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(ORDER_KEY);
    }

    @Bean
    MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    Queue stopRabbitQueue(){
        Queue orderQueue = new Queue(STOP_ORDER_QUEUE, true);
        return orderQueue;
    }

    @Bean
    DirectExchange stopRabbitExchange(){
        return new DirectExchange(STOP_ORDER_EXCHANGE);
    }

    @Bean
    Binding stopBind(@Qualifier("stopRabbitQueue") Queue queue, @Qualifier("stopRabbitExchange") DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(STOP_ORDER_KEY);
    }


    public final static String OFFER_QUEUE="offerQueue";

    public final static String OFFER_MADE_QUEUE="offerMadeQueue";

    @Bean(name = "offerQueue")
    Queue rabbitOfferQueue(){
        Queue orderQueue = new Queue(OFFER_QUEUE, true);
        return orderQueue;
    }

    @Bean(name = "offerMadeQueue")
    Queue offerMadeQueue(){
        Queue orderQueue = new Queue(OFFER_MADE_QUEUE, true);
        return orderQueue;
    }


    @Bean
    Queue offerReplyQueue(){
        Queue orderQueue = new Queue(OFFER_REPLY_QUEUE, true);
        return orderQueue;
    }

    @Bean
    DirectExchange offerReplyRabbitExchange(){
        return new DirectExchange(OFFER_REPLY_EXCHANGE);
    }

    @Bean
    Binding offerReplyBind(@Qualifier("offerReplyQueue") Queue queue, @Qualifier("offerReplyRabbitExchange") DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(OFFER_REPLY_KEY);
    }

}
