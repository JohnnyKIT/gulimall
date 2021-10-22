package com.situjunjie.gulimallseckill.scheduled;

import com.situjunjie.gulimallseckill.service.SeckillSkuService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 秒杀商品处理相关的计划任务
 */
@Component
public class SeckillSkuScheduled {

    @Autowired
    SeckillSkuService seckillSkuService;

    @Autowired
    RedissonClient redissonClient;

    public static final String UPLOAD_SECKILL_LOCK = "UPLOAD_SECKILL_LOCK";

    @Scheduled(cron = "0 * * * * ?")//每天凌晨3点定时执行
    public void uploadSeckillSkuLast3Days(){
        RLock lock = redissonClient.getLock(UPLOAD_SECKILL_LOCK);
        lock.lock(10l, TimeUnit.SECONDS);
        try{
            seckillSkuService.uploadSeckillSkuLast3Days();
        }finally {
            lock.unlock();
        }



    }
}
