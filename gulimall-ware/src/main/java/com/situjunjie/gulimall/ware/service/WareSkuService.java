package com.situjunjie.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.situjunjie.common.to.SkuHasStock;
import com.situjunjie.common.utils.PageUtils;
import com.situjunjie.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.situjunjie.gulimall.ware.entity.WareSkuEntity;
import com.situjunjie.gulimall.ware.vo.LockOrderStockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author situjunjie
 * @email situjunjie@vip.qq.com
 * @date 2021-06-16 13:33:09
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageCondition(Map<String, Object> params);

    List<SkuHasStock> skuHasStock(List<Long> skuIds);

    void lockOrderStock(LockOrderStockVo vo);

    void releaseStock(WareOrderTaskDetailEntity entity);
}

