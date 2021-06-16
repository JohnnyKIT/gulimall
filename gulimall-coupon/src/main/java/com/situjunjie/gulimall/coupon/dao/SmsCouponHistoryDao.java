package com.situjunjie.gulimall.coupon.dao;

import com.situjunjie.gulimall.coupon.entity.SmsCouponHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券领取历史记录
 * 
 * @author situjunjie
 * @email situjunjie@vip.qq.com
 * @date 2021-06-15 21:45:26
 */
@Mapper
public interface SmsCouponHistoryDao extends BaseMapper<SmsCouponHistoryEntity> {
	
}
