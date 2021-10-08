package com.situjunjie.gulimall.ware.listener;

import com.rabbitmq.client.Channel;
import com.situjunjie.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.situjunjie.gulimall.ware.service.WareSkuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;


/**
 * 用于监听解锁库存队列的监听器
 */
@RabbitListener(queues = "stock.release.stock.queue")
@Configuration
@Slf4j
public class StockUnlockListener {

    @Autowired
    WareSkuService wareSkuService;

    @RabbitHandler
    public void unLockStock(WareOrderTaskDetailEntity entity, Channel channel, Message message){
        log.info("收到库存工作单==>{}",entity);
        try {
            //解锁库存
            wareSkuService.releaseStock(entity);
            //接收消息应答
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
