package com.mycode.tourapptelegrambot.configForProduction.production;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Ali Guliyev
 * @version 1.0
 * @implNote publisher configuration class
 */

@Configuration
@Profile("!dev")
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



    public final static String OFFER_QUEUE="offerQueue";
    public final static String OFFER_EXCHANGE="offerExchange";
    public final static String OFFER_KEY="offerKey";

    public final static String OFFER_MADE_QUEUE="offerMadeQueue";
    public final static String OFFER_MADE_EXCHANGE="offerMadeExchange";
    public final static String OFFER_MADE_KEY="offerMadeKey";

    @Bean(name = "offerQueue")
    Queue rabbitOfferQueue(){
        Queue orderQueue = new Queue(OFFER_QUEUE, true);
        return orderQueue;
    }

    @Bean(name = "offerExchange")
    DirectExchange rabbitOfferExchange(){
        return new DirectExchange(OFFER_EXCHANGE);
    }

    @Bean(name = "offerBind")
    Binding offerBind(@Qualifier("offerQueue") Queue queue, @Qualifier("offerExchange") DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(OFFER_KEY);
    }


    @Bean(name = "offerMadeQueue")
    Queue offerMadeQueue(){
        Queue orderQueue = new Queue(OFFER_MADE_QUEUE, true);
        return orderQueue;
    }

    @Bean(name = "offerMadeExchange")
    DirectExchange offerMadeExchange(){
        return new DirectExchange(OFFER_MADE_EXCHANGE);
    }

    @Bean(name = "offerMadeBind")
    Binding offerMadeBind(@Qualifier("offerMadeQueue") Queue queue, @Qualifier("offerMadeExchange") DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(OFFER_MADE_KEY);
    }


    @Bean
    public ConnectionFactory connectionFactory() throws URISyntaxException {
        final URI rabbitMqUrl = new URI(System.getenv("CLOUDAMQP_URL"));
        final CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setUri(rabbitMqUrl);
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() throws URISyntaxException {
        RabbitTemplate temp = new RabbitTemplate(connectionFactory());
        temp.setMessageConverter(messageConverter());
        return temp;
    }

    @Bean
    MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

}
