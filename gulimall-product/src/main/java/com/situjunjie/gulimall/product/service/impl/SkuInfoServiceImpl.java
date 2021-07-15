package com.situjunjie.gulimall.product.service.impl;

import com.situjunjie.common.to.es.SkuEsModel;
import com.situjunjie.gulimall.product.entity.SkuImagesEntity;
import com.situjunjie.gulimall.product.entity.SpuInfoDescEntity;
import com.situjunjie.gulimall.product.service.*;
import com.situjunjie.gulimall.product.vo.SkuItemSaleAttrVo;
import com.situjunjie.gulimall.product.vo.SkuItemVo;
import com.situjunjie.gulimall.product.vo.SpuItemAttrGroupVo;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.util.StringUtils;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    ProductAttrValueService productAttrValueService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

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

    @Override
    public SkuItemVo querySkuItemVoInfo(Long skuId) {
        SkuItemVo skuItemVo = new SkuItemVo();
        //组装skuInfo信息
        SkuInfoEntity skuInfo = this.baseMapper.selectById(skuId);
        skuItemVo.setInfo(skuInfo);
        //组装SpuDesc信息
        SpuInfoDescEntity spuInfoDesc = spuInfoDescService.getById(skuInfo.getSpuId());
        skuItemVo.setDesc(spuInfoDesc);
        //组装Sku图片信息
        List<SkuImagesEntity> skuImages = skuImagesService.list(new QueryWrapper<SkuImagesEntity>().eq("sku_id", skuId));
        skuItemVo.setImages(skuImages);
        //组装常规属性信息
        List<SpuItemAttrGroupVo> spuItemAttrGroupVos =productAttrValueService.getAttrGroupValue(skuInfo.getSpuId(),skuInfo.getCatalogId());
        skuItemVo.setGroupAttrs(spuItemAttrGroupVos);
        //封装销售属性
        List<SkuItemSaleAttrVo> skuItemSaleAttrVos = skuSaleAttrValueService.getSaleAttrGoupBySpuId(skuInfo.getSpuId());
        skuItemVo.setSaleAttr(skuItemSaleAttrVos);

        return skuItemVo;
    }


}