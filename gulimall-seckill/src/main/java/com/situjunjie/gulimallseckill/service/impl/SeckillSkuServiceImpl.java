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
import com.situjunjie.gulimallseckill.vo.SeckillSkuRelationEntity;
import com.situjunjie.gulimallseckill.vo.to.SeckillSessionInfoTo;
import com.situjunjie.gulimallseckill.vo.to.SeckillSessionRedisVo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
                    List<String> seckill = new ArrayList<String>();
                    session.getRelationSkus().stream().forEach(seckillSku->{
                        //1.第一步需要缓存活动信息
                        seckill.add(session.getId()+"_"+seckillSku.getId());
                        SeckillSessionRedisVo sessionVo = new SeckillSessionRedisVo();
                        BeanUtils.copyProperties(session,sessionVo);

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
                    saveSeckillSession(session,seckill);



                });

            }

        }
    }

    /**
     * 获取当前活动的所有秒杀商品
     * @return
     */
    @Override
    public List<SeckillSkuRedisVo> getCurrentSeckillSku() {
        Long currentTime = new Date().getTime();
        //查询所有秒杀活动的key
        Set<String> keys = redisTemplate.keys(SESSION_INFO_REDIS_PREFIX + "*");
        //筛选得到所有当前正在进行的活动
        keys.stream().forEach(key -> {
            String[] strings = key.replace(SESSION_INFO_REDIS_PREFIX, "").split("_");
            Long startDate = Long.parseLong(strings[0]);
            Long endDate = Long.parseLong(strings[1]);
            if (currentTime > startDate && currentTime < endDate) {
                //活动是当前的

            }
        });
        List<String> seckillSessions = redisTemplate.opsForValue().multiGet(keys);

        return null;
    }

    /**
     * 保存秒杀活动信息到Redis
     * @param session
     * @param seckillSku
     */
    private void saveSeckillSession(SeckillSessionInfoTo session, List<String> seckillSku){

        String key =SESSION_INFO_REDIS_PREFIX+session.getStartTime().getTime()+"_"+session.getEndTime().getTime();
        if(!redisTemplate.hasKey(key)){
            redisTemplate.opsForList().leftPushAll(key,seckillSku);
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
