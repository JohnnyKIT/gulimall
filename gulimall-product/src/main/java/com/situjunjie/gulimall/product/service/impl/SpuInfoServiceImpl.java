package com.situjunjie.gulimall.product.service.impl;

import com.situjunjie.gulimall.product.entity.*;
import com.situjunjie.gulimall.product.service.*;
import com.situjunjie.gulimall.product.vo.BaseAttrs;
import com.situjunjie.gulimall.product.vo.Images;
import com.situjunjie.gulimall.product.vo.Skus;
import com.situjunjie.gulimall.product.vo.SpuSaveVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.situjunjie.common.utils.PageUtils;
import com.situjunjie.common.utils.Query;

import com.situjunjie.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    SpuImagesService spuImagesService;

    @Autowired
    AttrService attrService;

    @Autowired
    ProductAttrValueService productAttrValueService;

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void saveSpuInfo(SpuSaveVo spuInfo) {

        //1 保存SpuInfo
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuInfo,spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.save(spuInfoEntity);

        //2. 保存Spu描述图片信息
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        List<String> decript = spuInfo.getDecript();
        descEntity.setSpuId(spuInfoEntity.getId());
        descEntity.setDecript(String.join(",",decript));
        spuInfoDescService.save(descEntity);

        //3.保存Spu 图片集
        List<String> images = spuInfo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(),images);
        
        //4.保存spu的基本属性
        List<BaseAttrs> baseAttrs = spuInfo.getBaseAttrs();
        if(baseAttrs!=null && baseAttrs.size()>0){
            List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
                ProductAttrValueEntity entity = new ProductAttrValueEntity();
                entity.setAttrId(attr.getAttrId());
                entity.setAttrName(attrService.getById(attr.getAttrId()).getAttrName());
                entity.setAttrValue(attr.getAttrValues());
                entity.setQuickShow(attr.getShowDesc());
                entity.setSpuId(spuInfoEntity.getId());
                return entity;
            }).collect(Collectors.toList());
            productAttrValueService.saveBatch(collect);

        }
        
        //5. 保存sku的基本属性
        List<Skus> skus = spuInfo.getSkus();
        if(skus!=null && skus.size()>0){
            skus.forEach(sku->{
                String defaultimg ="";
                for (Images image : sku.getImages()) {
                    if(image.getDefaultImg()==1){
                        defaultimg = image.getImgUrl();
                        break;
                    }
                }
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku,skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfo.getBrandId());
                skuInfoEntity.setSaleCount(0l);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultimg);
                skuInfoService.save(skuInfoEntity);
                Long skuId = skuInfoEntity.getSkuId();

                //5.2保存所有sku的图片
                List<SkuImagesEntity> collect = sku.getImages().stream().map(image -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(image.getImgUrl());
                    skuImagesEntity.setDefaultImg(image.getDefaultImg());
                    return skuImagesEntity;
                }).collect(Collectors.toList());
                skuImagesService.saveBatch(collect);

                //5.3保存所有sku销售属性
                List<SkuSaleAttrValueEntity> skuAttrs = sku.getAttr().stream().map(attr -> {
                    SkuSaleAttrValueEntity saleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(attr, saleAttrValueEntity);
                    saleAttrValueEntity.setSkuId(skuId);
                    return saleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuAttrs);


            });
        }


    }

}