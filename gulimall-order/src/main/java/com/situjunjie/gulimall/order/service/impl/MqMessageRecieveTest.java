package com.situjunjie.gulimall.order.service.impl;


import com.rabbitmq.client.Channel;
import com.situjunjie.gulimall.order.entity.OrderEntity;
import com.situjunjie.gulimall.order.entity.OrderReturnApplyEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
@Slf4j
@RabbitListener(queues = {"hello-java-queue"})
public class MqMessageRecieveTest {

    @RabbitHandler
    public void receiveOrderMessage(OrderEntity orderEntity, Channel channel, Message message){
        log.info("接收到消息={}",orderEntity);
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RabbitHandler
    public void receiveOrderMessage(OrderReturnApplyEntity orderReturnApplyEntity, Channel channel, Message message){
        log.info("接收到消息={}",orderReturnApplyEntity);
        try {
            //channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
