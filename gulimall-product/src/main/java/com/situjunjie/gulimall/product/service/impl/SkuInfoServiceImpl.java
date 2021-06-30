package com.situjunjie.gulimall.product.service.impl;

import com.situjunjie.common.to.es.SkuEsModel;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.situjunjie.common.utils.PageUtils;
import com.situjunjie.common.utils.Query;

import com.situjunjie.gulimall.product.dao.SkuInfoDao;
import com.situjunjie.gulimall.product.entity.SkuInfoEntity;
import com.situjunjie.gulimall.product.service.SkuInfoService;
import org.springframework.util.StringUtils;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        /**
         * key:
         * catelogId: 0
         * brandId: 0
         * min: 0
         * max: 0
         */
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<SkuInfoEntity>();
        String key = (String) params.get("key");
        String brandId = (String) params.get("brandId");
        String min = (String) params.get("min");
        String max = (String) params.get("max");
        if(!StringUtils.isEmpty(key)){
            wrapper.and(w->{w.eq("sku_id",key).or().like("sku_title",key);});
        }
        if(!StringUtils.isEmpty(brandId)&&!"0".equals(brandId)){
            wrapper.eq("brand_id",brandId);
        }
        if(!StringUtils.isEmpty(min)){
            BigDecimal val1 = new BigDecimal(min);
            if(val1.compareTo(new BigDecimal("0"))==1){
                wrapper.ge("price",val1);
            }
        }if(!StringUtils.isEmpty(max)){
            BigDecimal val2 = new BigDecimal(max);
            if(val2.compareTo(new BigDecimal("0"))==1){
                wrapper.le("price",val2);
            }
        }


        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }



}