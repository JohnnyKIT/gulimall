package com.situjunjie.gulimall.product.dao;

import com.situjunjie.gulimall.product.entity.ProductAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.situjunjie.gulimall.product.vo.SpuItemAttrGroupVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * spu属性值
 * 
 * @author situjunjie
 * @email situjunjie@vip.qq.com
 * @date 2021-06-16 13:30:29
 */
@Mapper
public interface ProductAttrValueDao extends BaseMapper<ProductAttrValueEntity> {

    List<SpuItemAttrGroupVo> querySpuAttrGroupByCatelogAndSpu(@Param("spuId") Long spuId, @Param("catelogId") Long catalogId);
}
