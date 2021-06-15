package com.situjunjie.gulimall.coupon.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.situjunjie.common.utils.PageUtils;
import com.situjunjie.common.utils.Query;

import com.situjunjie.gulimall.coupon.dao.SmsCouponSpuRelationDao;
import com.situjunjie.gulimall.coupon.entity.SmsCouponSpuRelationEntity;
import com.situjunjie.gulimall.coupon.service.SmsCouponSpuRelationService;


@Service("smsCouponSpuRelationService")
public class SmsCouponSpuRelationServiceImpl extends ServiceImpl<SmsCouponSpuRelationDao, SmsCouponSpuRelationEntity> implements SmsCouponSpuRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SmsCouponSpuRelationEntity> page = this.page(
                new Query<SmsCouponSpuRelationEntity>().getPage(params),
                new QueryWrapper<SmsCouponSpuRelationEntity>()
        );

        return new PageUtils(page);
    }

}