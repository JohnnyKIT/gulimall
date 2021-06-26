package com.situjunjie.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.situjunjie.common.utils.PageUtils;
import com.situjunjie.gulimall.product.entity.BrandEntity;
import com.situjunjie.gulimall.product.entity.CategoryBrandRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author situjunjie
 * @email situjunjie@vip.qq.com
 * @date 2021-06-16 13:30:29
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryBrandRelationEntity> queryByBrandId(Map<String, Object> params);

    void update(Long brandId, String name);

    void updateCategory(Long catId, String name);

    List<BrandEntity> listRelatedBrandByCateId(Long catId);
}

