package com.situjunjie.gulimall.order.controller;

import com.situjunjie.gulimall.order.entity.OrderEntity;
import com.situjunjie.gulimall.order.entity.OrderReturnApplyEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * 测试MQ发送
 */
@RestController
@Slf4j
public class RabbitMQController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @GetMapping("/sendMQ")
    public String sendMQMessage(@RequestParam(value = "num",defaultValue = "10")Integer num){
        for (Integer i = 0; i < num; i++) {
            if(i%2==0){
                OrderEntity order = new OrderEntity();
                order.setOrderSn(UUID.randomUUID().toString());
                rabbitTemplate.convertAndSend("hello-java-exchange","test",order);
                log.info("消息发送成功");
            }else{
                OrderReturnApplyEntity entity = new OrderReturnApplyEntity();
                entity.setOrderSn(UUID.randomUUID().toString());
                rabbitTemplate.convertAndSend("hello-java-exchange","test",entity);
                log.info("消息发送成功");
            }
        }
        return "ok";
    }
}
