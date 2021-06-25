package com.situjunjie.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.situjunjie.common.utils.PageUtils;
import com.situjunjie.gulimall.product.entity.AttrEntity;
import com.situjunjie.gulimall.product.vo.AttrRespVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author situjunjie
 * @email situjunjie@vip.qq.com
 * @date 2021-06-16 13:30:30
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long catId,String type);

    AttrRespVo getAttrInfo(Long attrId);

    List<AttrEntity> getAttrRelation(Long attrGroupId);

    PageUtils getRelatableAttr(Long attrGroupId,Map<String,Object> params);
}

