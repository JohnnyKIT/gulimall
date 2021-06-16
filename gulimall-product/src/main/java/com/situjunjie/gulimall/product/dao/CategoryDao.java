package com.situjunjie.gulimall.product.dao;

import com.situjunjie.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author situjunjie
 * @email situjunjie@vip.qq.com
 * @date 2021-06-16 13:30:30
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
