package com.situjunjie.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.situjunjie.common.to.SkuReductionTo;
import com.situjunjie.common.utils.PageUtils;
import com.situjunjie.gulimall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author situjunjie
 * @email situjunjie@vip.qq.com
 * @date 2021-06-16 13:31:16
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveInfo(SkuReductionTo skuReductionTo);
}

