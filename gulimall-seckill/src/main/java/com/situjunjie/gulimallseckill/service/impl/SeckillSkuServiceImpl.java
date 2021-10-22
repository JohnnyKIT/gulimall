package com.situjunjie.gulimallseckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.situjunjie.common.to.SkuInfoEntity;
import com.situjunjie.common.to.SkuInfoTo;
import com.situjunjie.common.utils.R;
import com.situjunjie.gulimallseckill.feign.ProductFeignService;
import com.situjunjie.gulimallseckill.service.SeckillSkuService;
import com.situjunjie.gulimallseckill.feign.CouponFeignService;
import com.situjunjie.gulimallseckill.vo.SeckillSkuRedisVo;
import com.situjunjie.gulimallseckill.vo.to.SeckillSessionInfoTo;
import com.situjunjie.gulimallseckill.vo.to.SeckillSessionRedisVo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 处理秒杀商品的Service
 */
@Service
@Slf4j
public class SeckillSkuServiceImpl implements SeckillSkuService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    RedissonClient redissonClient;

    public static final String SESSION_INFO_REDIS_PREFIX = "seckill:session:";

    public static final String SKU_CACHE_REDIS_OPS = "seckill:skuInfo:";

    public static final String SESSION_SKU_SEMAPHORE = "seckill:skuSemaphore:";

    @Override
    public void uploadSeckillSkuLast3Days() {
        R r = couponFeignService.getSeckillSessionLast3Days();
        if(r.getCode()==0){//请求成功
            List<SeckillSessionInfoTo> sessions = r.getData(new TypeReference<List<SeckillSessionInfoTo>>() {
            });
            if(sessions!=null && !sessions.isEmpty()){
                sessions.stream().forEach(session->{
                //1.第一步需要缓存活动信息
                    SeckillSessionRedisVo sessionVo = new SeckillSessionRedisVo();
                    BeanUtils.copyProperties(session,sessionVo);
                    saveSeckillSession(sessionVo);
                //2.第二部缓存活动的商品信息
                    List<SeckillSkuRedisVo> skuVos = new ArrayList<SeckillSkuRedisVo>();
                    session.getRelationSkus().stream().forEach(seckillSku->{
                        SeckillSkuRedisVo skuRedisVo = new SeckillSkuRedisVo();
                        BeanUtils.copyProperties(seckillSku,skuRedisVo);
                        R skuInfo = productFeignService.getSkuInfo(seckillSku.getSkuId());
                        skuRedisVo.setSkuInfo(skuInfo.getData("skuInfo",new TypeReference<SkuInfoEntity>(){}));
                        //装配随机码
                        String token = UUID.randomUUID().toString().replace("-", "");
                        skuRedisVo.setToken(token);
                        //装配秒杀开始和结束时间
                        skuRedisVo.setStartDate(session.getStartTime());
                        skuRedisVo.setEndDate(session.getEndTime());
                        saveSeckillSkuInfo(skuRedisVo);
                        //通过Redisson分布式锁-信号量控制秒杀请求
                        if(!redisTemplate.hasKey(SESSION_SKU_SEMAPHORE + skuRedisVo.getPromotionSessionId()+"_"+skuRedisVo.getSkuId().toString())){
                            RSemaphore semaphore = redissonClient.getSemaphore(SESSION_SKU_SEMAPHORE + skuRedisVo.getPromotionSessionId()+"_"+skuRedisVo.getSkuId().toString());
                            semaphore.trySetPermits(skuRedisVo.getSeckillLimit().intValue());
                        }

                    });


                });

            }

        }
    }

    /**
     * 保存秒杀活动信息到Redis
     * @param session
     */
    private void saveSeckillSession(SeckillSessionRedisVo session){

        String key =SESSION_INFO_REDIS_PREFIX+session.getStartTime().getTime()+"_"+session.getEndTime().getTime();
        String value = JSON.toJSONString(session);
        if(!redisTemplate.hasKey(key)){ //防止重复上传秒杀活动
            log.info("每日秒杀活动信息保存至Redis==>{}",session);
            redisTemplate.opsForValue().set(key,value);
        }
    }

    private void saveSeckillSkuInfo(SeckillSkuRedisVo skuInfoVo){

        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(SKU_CACHE_REDIS_OPS+skuInfoVo.getId());
        String key = skuInfoVo.getPromotionSessionId()+"_"+skuInfoVo.getId().toString();
        if(!ops.hasKey(key)){
            log.info("每日秒杀活动信息保存至Redis==>",skuInfoVo);
            ops.put(key,JSON.toJSONString(skuInfoVo));
        }



    }
}
