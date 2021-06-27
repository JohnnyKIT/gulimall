package com.situjunjie.gulimall.product.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.situjunjie.common.utils.PageUtils;
import com.situjunjie.common.utils.Query;

import com.situjunjie.gulimall.product.dao.ProductAttrValueDao;
import com.situjunjie.gulimall.product.entity.ProductAttrValueEntity;
import com.situjunjie.gulimall.product.service.ProductAttrValueService;
import org.springframework.transaction.annotation.Transactional;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<ProductAttrValueEntity> getAttrValueForSpu(Long spuId) {
        List<ProductAttrValueEntity> spu_id = this.list(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        return spu_id;
    }

    @Override
    @Transactional
    public void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> list) {

        this.remove(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id",spuId));
        List<ProductAttrValueEntity> collect = list.stream().map(item -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            BeanUtils.copyProperties(item, productAttrValueEntity);
            productAttrValueEntity.setSpuId(spuId);
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        this.saveBatch(collect);
    }


}