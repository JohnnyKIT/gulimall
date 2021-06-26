package com.situjunjie.gulimall.product.service.impl;

import com.situjunjie.gulimall.product.entity.ProductAttrValueEntity;
import com.situjunjie.gulimall.product.entity.SpuInfoDescEntity;
import com.situjunjie.gulimall.product.service.*;
import com.situjunjie.gulimall.product.vo.BaseAttrs;
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
import com.situjunjie.gulimall.product.entity.SpuInfoEntity;
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


    }

}