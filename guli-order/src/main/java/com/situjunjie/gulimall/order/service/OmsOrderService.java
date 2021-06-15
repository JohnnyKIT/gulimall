package com.situjunjie.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.situjunjie.common.utils.PageUtils;
import com.situjunjie.gulimall.order.entity.OmsOrderEntity;

import java.util.Map;

/**
 * 订单
 *
 * @author situjunjie
 * @email situjunjie@vip.qq.com
 * @date 2021-06-15 21:37:50
 */
public interface OmsOrderService extends IService<OmsOrderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

