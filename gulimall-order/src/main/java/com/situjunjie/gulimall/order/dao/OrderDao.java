package com.situjunjie.gulimall.order.dao;

import com.situjunjie.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author situjunjie
 * @email situjunjie@vip.qq.com
 * @date 2021-06-16 13:29:05
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
