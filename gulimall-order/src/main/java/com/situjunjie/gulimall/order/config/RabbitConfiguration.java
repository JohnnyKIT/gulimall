package com.situjunjie.gulimall.order.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@Slf4j
public class RabbitConfiguration {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }


    @PostConstruct
    public void initRabbitTemplate(){
        /**
         * 确认抵达brokker配置回调
         */
        rabbitTemplate.setConfirmCallback((correlationData,ack,cause)->{
            log.info("消息抵达服务器回调=>correlationData=[{}],ack=[{}],cause=[{}]",correlationData,ack,cause);
        });

        /**
         * 消息没有送达队列的错误回调
         */
        rabbitTemplate.setReturnCallback((message,replyCode,replyText,exchange,routingKey)->{
            log.info("消息没有送达队列回调=>message=[{}],replyCode=[{}],replyText=[{}],exchange=[{}],routingKey=[{}]",message,replyCode,replyText,exchange,routingKey);
        });

    }


}
