package com.situjunjie.gulimallseckill.service;

import com.situjunjie.gulimallseckill.vo.SeckillSkuRedisVo;

import java.util.List;

/**
 * 处理秒杀商品的Service接口
 */
public interface SeckillSkuService {
    void uploadSeckillSkuLast3Days();

    List<SeckillSkuRedisVo> getCurrentSeckillSku();

    SeckillSkuRedisVo getSkuSeckillInfoBySkuId(Long skuId);

    void kill(String killId, String token, Integer num);
}
