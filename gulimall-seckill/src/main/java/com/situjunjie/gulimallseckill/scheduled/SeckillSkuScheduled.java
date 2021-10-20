package com.situjunjie.gulimallseckill.scheduled;

import com.situjunjie.gulimallseckill.service.SeckillSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 秒杀商品处理相关的计划任务
 */
@Component
public class SeckillSkuScheduled {

    @Autowired
    SeckillSkuService seckillSkuService;

    @Scheduled(cron = "0 * * * * ?")//每天凌晨3点定时执行
    public void uploadSeckillSkuLast3Days(){
        seckillSkuService.uploadSeckillSkuLast3Days();

    }
}
