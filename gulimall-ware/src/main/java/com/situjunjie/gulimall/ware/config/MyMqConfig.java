package com.situjunjie.gulimall.ware.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MyMqConfig {

    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Exchange stockEventExchange(){
        return new TopicExchange("stock-event-exchange",true,false,null);
    }

    @Bean
    public Queue stockDelayQueue(){
        //String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
//        arguments.put("x-dead-letter-exchange","order-event-exchange");
//        arguments.put("x-dead-letter-routing-key","order.release.order");
//        arguments.put("x-message-ttl",60000);
        Map<String,Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange","stock-event-exchange");
        args.put("x-dead-letter-routing-key","stock.release");
        args.put("x-message-ttl",120000);
        return new Queue("stock.delay.queue",true,false,false,args);
    }

    @Bean
    public Queue stockReleaseStockQueue(){
        return new Queue("stock.release.stock.queue",true,false,false);
    }

    @Bean
    public Binding stockReleaseBinding(){
        //String destination, Binding.DestinationType destinationType, String exchange, String routingKey, Map<String, Object> arguments
        return new Binding("stock.release.stock.queue", Binding.DestinationType.QUEUE,"stock-event-exchange","stock.release.#",null);
    }

    @Bean
    public Binding stockLockBinding(){
        return new Binding("stock.delay.queue", Binding.DestinationType.QUEUE,"stock-event-exchange","stock.locked",null);
    }

    @RabbitListener
    public void testListener(Message message){
    }
}
