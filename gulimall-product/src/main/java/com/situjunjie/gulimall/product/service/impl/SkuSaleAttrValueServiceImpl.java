package com.situjunjie.gulimall.product.service.impl;

import com.situjunjie.gulimall.product.vo.SkuItemSaleAttrVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.situjunjie.common.utils.PageUtils;
import com.situjunjie.common.utils.Query;

import com.situjunjie.gulimall.product.dao.SkuSaleAttrValueDao;
import com.situjunjie.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.situjunjie.gulimall.product.service.SkuSaleAttrValueService;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuItemSaleAttrVo> getSaleAttrGoupBySpuId(Long spuId) {

        return  this.baseMapper.getAllSaleAttrGroupBySpuId(spuId);
    }

    @Override
    public List<String> getSaleAttrValueAsStringList(String skuId) {
        return this.baseMapper.getSaleAttrValueAsStringList(skuId);
    }

}