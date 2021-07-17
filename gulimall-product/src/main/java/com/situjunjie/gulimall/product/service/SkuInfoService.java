package com.situjunjie.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.situjunjie.common.utils.PageUtils;
import com.situjunjie.gulimall.product.entity.SkuInfoEntity;
import com.situjunjie.gulimall.product.vo.SkuItemVo;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * sku信息
 *
 * @author situjunjie
 * @email situjunjie@vip.qq.com
 * @date 2021-06-16 13:30:29
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageByCondition(Map<String, Object> params);

    SkuItemVo querySkuItemVoInfo(Long skuId) throws ExecutionException, InterruptedException;
}

