package com.individual_s7.post_service.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

    public static final String FRIENDSHIP_RESPONSE_EXCHANGE = "friendshipResponseExchange";
    public static final String POST_FRIENDSHIP_RESPONSE_QUEUE = "postFriendshipResponseQueue";
    public static final String FRIENDSHIP_RESPONSE_ROUTING_KEY = "friendshipResponseKey";

    @Bean
    public DirectExchange friendshipEventExchange() {
        return new DirectExchange(FRIENDSHIP_RESPONSE_EXCHANGE);
    }

    @Bean
    public Queue postFriendshipEventQueue() {
        return QueueBuilder.durable(POST_FRIENDSHIP_RESPONSE_QUEUE).build();
    }

    @Bean
    public Binding bindingEvent(Queue postFriendshipEventQueue, DirectExchange friendshipEventExchange) {
        return BindingBuilder.bind(postFriendshipEventQueue).to(friendshipEventExchange).with(FRIENDSHIP_RESPONSE_ROUTING_KEY);
    }

    public static final String USER_DELETE_EXCHANGE = "userDeleteExchange";
    public static final String USER_DELETE_QUEUE = "postUserDeleteQueue";
    public static final String USER_DELETE_ROUTING_KEY = "userDeleteKey";

    @Bean
    public DirectExchange userDeleteExchange() {
        return new DirectExchange(USER_DELETE_EXCHANGE);
    }

    @Bean
    public Queue userDeleteQueue() {
        return new Queue(USER_DELETE_QUEUE);
    }

    @Bean
    public Binding bindingDelete(Queue userDeleteQueue, DirectExchange userDeleteExchange) {
        return BindingBuilder.bind(userDeleteQueue).to(userDeleteExchange).with(USER_DELETE_ROUTING_KEY);
    }

    public static final String USER_UPDATE_EXCHANGE = "userUpdateExchange";
    public static final String USER_UPDATE_QUEUE = "postUserUpdateQueue";
    public static final String USER_UPDATE_ROUTING_KEY = "userUpdateKey";

    @Bean
    public DirectExchange userUpdateExchange() {
        return new DirectExchange(USER_UPDATE_EXCHANGE);
    }

    @Bean
    public Queue userUpdateQueue() {
        return new Queue(USER_UPDATE_QUEUE);
    }

    @Bean
    public Binding bindingUpdate(Queue userUpdateQueue, DirectExchange userUpdateExchange) {
        return BindingBuilder.bind(userUpdateQueue).to(userUpdateExchange).with(USER_UPDATE_ROUTING_KEY);
    }
}

