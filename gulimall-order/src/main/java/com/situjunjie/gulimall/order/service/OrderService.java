package com.situjunjie.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.situjunjie.common.to.MemberOrderReqTo;
import com.situjunjie.common.utils.PageUtils;
import com.situjunjie.gulimall.order.entity.OrderEntity;
import com.situjunjie.gulimall.order.vo.OrderSubmitResponseVo;
import com.situjunjie.gulimall.order.vo.OrderSubmitVo;
import com.situjunjie.gulimall.order.vo.PayAsyncVo;

import java.util.Map;

/**
 * 订单
 *
 * @author situjunjie
 * @email situjunjie@vip.qq.com
 * @date 2021-06-16 13:29:05
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    OrderSubmitResponseVo submitOrder(OrderSubmitVo vo);

    void releaseOrder(OrderEntity order);

    PageUtils queryMemberOrderPage(MemberOrderReqTo memberOrderReqTo);

    String payNotifyHandle(PayAsyncVo vo);
}

