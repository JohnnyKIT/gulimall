package com.situjunjie.gulimall.ware.service.impl;

import com.situjunjie.common.to.SkuHasStock;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.situjunjie.common.utils.PageUtils;
import com.situjunjie.common.utils.Query;

import com.situjunjie.gulimall.ware.dao.WareSkuDao;
import com.situjunjie.gulimall.ware.entity.WareSkuEntity;
import com.situjunjie.gulimall.ware.service.WareSkuService;
import org.springframework.util.StringUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageCondition(Map<String, Object> params) {
        /**
         * skuId:
         * wareId:
         */

        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        String wareId = (String) params.get("wareId");
        if(!StringUtils.isEmpty(skuId)){
            wrapper.eq("sku_id",skuId);
        }
        if(!StringUtils.isEmpty(wareId)){
            wrapper.eq("ware_id",wareId);
        }


        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuHasStock> skuHasStock(List<Long> skuIds) {

        List<SkuHasStock> collect = skuIds.stream().map(skuid -> {
            SkuHasStock skuHasStock = new SkuHasStock();
            skuHasStock.setSkuId(skuid);
            Long stock = this.baseMapper.selectSkuStock(skuid);
            skuHasStock.setHasStock(stock>0);
            return skuHasStock;
        }).collect(Collectors.toList());

        return collect;
    }

}