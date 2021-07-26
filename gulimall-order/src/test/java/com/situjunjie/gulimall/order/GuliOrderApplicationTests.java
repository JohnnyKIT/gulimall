package com.situjunjie.gulimall.order;


import com.situjunjie.common.to.MemberEntity;
import com.situjunjie.gulimall.order.entity.OrderReturnApplyEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class GuliOrderApplicationTests {

    @Autowired
    AmqpAdmin amqpAdmin;

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 测试发送消息
     */
    @Test
    public void testSendMessage(){
        OrderReturnApplyEntity orderReturnApplyEntity = new OrderReturnApplyEntity();
        orderReturnApplyEntity.setOrderId(1l);
        orderReturnApplyEntity.setReturnName("测试name");
        orderReturnApplyEntity.setCreateTime(new Date());
        rabbitTemplate.convertAndSend("hello-java-exchange","test",orderReturnApplyEntity);
        log.info("消息发送成功");
    }

    @Test
    public void contextLoads() {
        System.out.println("测试"+amqpAdmin);
    }

    @Test
    public void addExchange(){
        Exchange exchange = new DirectExchange("hello-java-exchange",true,false);
        amqpAdmin.declareExchange(exchange);
        log.info("测试交换机创建成功");
    }

    @Test
    public void addQueue(){
        Queue queue = new Queue("hello-java-queue",true,false,false);
        amqpAdmin.declareQueue(queue);
        log.info("测试队列创建成功");
    }

    @Test
    public void addBinding(){
        Binding binding = new Binding("hello-java-queue", Binding.DestinationType.QUEUE,"hello-java-exchange","test.#",null);
        amqpAdmin.declareBinding(binding);
    }

}
