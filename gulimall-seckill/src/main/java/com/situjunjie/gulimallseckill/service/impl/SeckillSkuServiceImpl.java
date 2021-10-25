package com.situjunjie.gulimallseckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.situjunjie.common.to.MemberEntity;
import com.situjunjie.common.to.SkuInfoEntity;
import com.situjunjie.common.to.SkuInfoTo;
import com.situjunjie.common.utils.R;
import com.situjunjie.gulimallseckill.feign.ProductFeignService;
import com.situjunjie.gulimallseckill.interceptor.LoginUserInterceptor;
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
import java.util.concurrent.TimeUnit;
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

    public static final String SECKILL_ORDERD = "seckill:orderd:";

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
        List<SeckillSkuRedisVo> rs = new ArrayList<>();
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
                List<String> skuHashKey = redisTemplate.opsForList().range(key, -100, 100);
                if (skuHashKey!=null && !skuHashKey.isEmpty()){
                    String sessionId = skuHashKey.get(0).split("_")[0];
                    skuHashKey.stream().forEach(hashKey->{
                        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKU_CACHE_REDIS_OPS + sessionId);
                        String skuInfoStr = hashOps.get(hashKey);
                        SeckillSkuRedisVo skuRedisVo = JSON.parseObject(skuInfoStr, SeckillSkuRedisVo.class);
                        skuRedisVo.setToken(null);
                        rs.add(skuRedisVo);
                    });
                }
            }
        });
        return rs;
    }

    /**
     * 根据skuId获取对应sku的秒杀信息
     * @param skuId
     * @return
     */
    @Override
    public SeckillSkuRedisVo getSkuSeckillInfoBySkuId(Long skuId) {
        //获得这个sku的所有秒杀商品信息
        Set<String> seckillSkuHashKeys = redisTemplate.keys(SKU_CACHE_REDIS_OPS + "*");
        ArrayList<SeckillSkuRedisVo> seckillVos = new ArrayList<>();
        seckillSkuHashKeys.stream().forEach(hashKey->{
            BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(hashKey);
            List<String> jsonStrList = hashOps.multiGet(hashOps.keys());
            jsonStrList.stream().forEach(jsonStr -> {
                SeckillSkuRedisVo skuRedisVo = JSON.parseObject(jsonStr, SeckillSkuRedisVo.class);
                if (skuId.equals(skuRedisVo.getSkuId())) {
                    seckillVos.add(skuRedisVo);
                }
            });
        });
        //从同一个商品多个秒杀活动选取最近开始的秒杀信息进行返回
        return selectLastestSeckillSku(seckillVos);
    }

    /**
     * 秒杀主业务方法
     * @param killId  1_1
     * @param token 随机码
     * @param num 秒杀数量
     */
    @Override
    public void kill(String killId, String token, Integer num) {

        MemberEntity user = LoginUserInterceptor.threadLocal.get();

        //1.获取秒杀商品具体信息
        String sessionId = killId.split("_")[0];
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKU_CACHE_REDIS_OPS + sessionId);
        String jsonStr = hashOps.get(killId);
        //得到秒杀商品详情信息
        SeckillSkuRedisVo seckillSkuInfo = JSON.parseObject(jsonStr, SeckillSkuRedisVo.class);
        //2. 校验时间合法性
        Date current = new Date();
        if(current.after(seckillSkuInfo.getStartDate()) && current.before(seckillSkuInfo.getEndDate())){
            //处于秒杀时间范围内 继续校验token随机码
            if(seckillSkuInfo.getToken().equals(token)){
                //校验是否买过了
                redisTemplate.opsForValue().setIfAbsent(SECKILL_ORDERD + user.getId(), num.toString());
                //随机码校验成功 准备开始占用Semaphore信号量
                RSemaphore semaphore = redissonClient.getSemaphore(SESSION_SKU_SEMAPHORE + "_" + sessionId + "_" + seckillSkuInfo.getSkuId());
                try {
                    semaphore.tryAcquire(num,seckillSkuInfo.getEndDate().getTime()-current.getTime(), TimeUnit.MILLISECONDS);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 从同一个商品多个秒杀活动选取最近开始的秒杀信息进行返回
     * @param seckillVos
     * @return
     */
    private SeckillSkuRedisVo selectLastestSeckillSku(ArrayList<SeckillSkuRedisVo> seckillVos) {
        if(seckillVos==null || seckillVos.isEmpty()){
            return null;
        }
        Date current = new Date();
        SeckillSkuRedisVo seckillSku = null;
        for (SeckillSkuRedisVo item: seckillVos
             ) {
            if (item.getEndDate().after(current)){//过滤已结束的秒杀场次
                if (seckillSku==null){
                    seckillSku = item;
                }
                long start = seckillSku.getStartDate().getTime();
                if(start<current.getTime()){
                    //直接返回已经开始的
                    return item;
                }else{
                    if(item.getStartDate().getTime()<seckillSku.getStartDate().getTime()){
                        seckillSku = item;
                    }
                }


            }
        }
        return seckillSku;
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

        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(SKU_CACHE_REDIS_OPS+skuInfoVo.getPromotionSessionId());
        String key = skuInfoVo.getPromotionSessionId()+"_"+skuInfoVo.getId().toString();
        if(!ops.hasKey(key)){
            log.info("每日秒杀活动信息保存至Redis==>",skuInfoVo);
            ops.put(key,JSON.toJSONString(skuInfoVo));
        }



    }
}