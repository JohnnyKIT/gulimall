package com.situjunjie.gulimall.product.dao;

import com.situjunjie.gulimall.product.entity.CommentReplayEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品评价回复关系
 * 
 * @author situjunjie
 * @email situjunjie@vip.qq.com
 * @date 2021-06-15 20:31:09
 */
@Mapper
public interface CommentReplayDao extends BaseMapper<CommentReplayEntity> {
	
}
