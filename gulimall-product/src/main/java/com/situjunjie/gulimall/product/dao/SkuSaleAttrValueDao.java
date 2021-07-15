package com.situjunjie.gulimall.product.dao;

import com.situjunjie.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.situjunjie.gulimall.product.vo.SkuItemSaleAttrVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 * 
 * @author situjunjie
 * @email situjunjie@vip.qq.com
 * @date 2021-06-16 13:30:29
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    List<SkuItemSaleAttrVo> getAllSaleAttrGroupBySpuId(@Param("spuId")Long spuId);
}
