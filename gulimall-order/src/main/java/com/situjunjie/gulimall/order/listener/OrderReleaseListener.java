package com.situjunjie.gulimall.order.listener;

import com.rabbitmq.client.Channel;
import com.situjunjie.gulimall.order.entity.OrderEntity;
import com.situjunjie.gulimall.order.service.OrderService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@RabbitListener(queues = "order.release.order.queue")
public class OrderReleaseListener {

    @Autowired
    OrderService orderService;

    @RabbitHandler
    public void releaseOrder(OrderEntity order, Channel channel, Message message) throws IOException {
        orderService.releaseOrder(order);
        System.out.println("收到过期订单 "+order.getOrderSn());
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);

    }
}
