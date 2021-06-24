package com.situjunjie.gulimall.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.situjunjie.common.utils.PageUtils;
import com.situjunjie.common.utils.Query;

import com.situjunjie.gulimall.product.dao.CategoryBrandRelationDao;
import com.situjunjie.gulimall.product.entity.CategoryBrandRelationEntity;
import com.situjunjie.gulimall.product.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<CategoryBrandRelationEntity> wrapper = new QueryWrapper<>();

       // wrapper.eq("brand_id",params.get("brandId"));
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryBrandRelationEntity> queryByBrandId(Map<String, Object> params) {
        QueryWrapper<CategoryBrandRelationEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("brand_id",params.get("brandId"));
        List<CategoryBrandRelationEntity> list = baseMapper.selectList(wrapper);
        return list;
    }

    @Override
    public void update(Long brandId, String name) {

        CategoryBrandRelationEntity entity = new CategoryBrandRelationEntity();
        entity.setBrandId(brandId);
        entity.setBrandName(name);
        QueryWrapper<CategoryBrandRelationEntity> wrapper = new QueryWrapper<>();
        baseMapper.update(entity,wrapper);

    }

    @Override
    public void updateCategory(Long catId, String name) {
        baseMapper.updateCategory(catId,name);
    }

}