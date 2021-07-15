package com.situjunjie.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.situjunjie.common.utils.PageUtils;
import com.situjunjie.gulimall.product.entity.ProductAttrValueEntity;
import com.situjunjie.gulimall.product.vo.SpuItemAttrGroupVo;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author situjunjie
 * @email situjunjie@vip.qq.com
 * @date 2021-06-16 13:30:29
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<ProductAttrValueEntity> getAttrValueForSpu(Long spuId);

    void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> list);

    List<SpuItemAttrGroupVo> getAttrGroupValue(Long spuId, Long catalogId);
}

