package com.situjunjie.gulimall.coupon.service.impl;

import com.situjunjie.common.to.SkuReductionTo;
import com.situjunjie.gulimall.coupon.entity.MemberPriceEntity;
import com.situjunjie.gulimall.coupon.entity.SkuLadderEntity;
import com.situjunjie.gulimall.coupon.service.MemberPriceService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.situjunjie.common.utils.PageUtils;
import com.situjunjie.common.utils.Query;

import com.situjunjie.gulimall.coupon.dao.SkuFullReductionDao;
import com.situjunjie.gulimall.coupon.entity.SkuFullReductionEntity;
import com.situjunjie.gulimall.coupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    SkuLadderServiceImpl skuLadderService;

    @Autowired
    MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveInfo(SkuReductionTo skuReductionTo) {

        //1.保存满减打折 会员价
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setSkuId(skuReductionTo.getSkuId());
        skuLadderEntity.setFullCount(skuReductionTo.getFullCount());
        skuLadderEntity.setDiscount(skuReductionTo.getDiscount());
        skuLadderEntity.setAddOther(skuReductionTo.getCountStatus());
        if(skuLadderEntity.getFullCount()>0){

        skuLadderService.save(skuLadderEntity);
        }

        SkuFullReductionEntity reductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTo,reductionEntity);
        if(reductionEntity.getFullPrice().compareTo(new BigDecimal("0"))==0){

        this.save(reductionEntity);
        }

        List<MemberPriceEntity> collect = skuReductionTo.getMemberPrice().stream().map(item -> {
            MemberPriceEntity entity = new MemberPriceEntity();
            entity.setSkuId(skuReductionTo.getSkuId());
            entity.setMemberLevelId(item.getId());
            entity.setMemberLevelName(item.getName());
            entity.setMemberPrice(item.getPrice());
            entity.setAddOther(1);
            return entity;
        }).filter(item->{
            return item.getMemberPrice().compareTo(new BigDecimal("0"))==1;
        }).collect(Collectors.toList());

        memberPriceService.saveBatch(collect);

    }

}