package com.situjunjie.gulimall.ware.service.impl;

import com.situjunjie.gulimall.ware.entity.WareSkuEntity;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.situjunjie.common.utils.PageUtils;
import com.situjunjie.common.utils.Query;

import com.situjunjie.gulimall.ware.dao.PurchaseDetailDao;
import com.situjunjie.gulimall.ware.entity.PurchaseDetailEntity;
import com.situjunjie.gulimall.ware.service.PurchaseDetailService;
import org.springframework.util.StringUtils;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                new QueryWrapper<PurchaseDetailEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageCondition(Map<String, Object> params) {
        /**
         * status:
         * wareId:
         */
        QueryWrapper<PurchaseDetailEntity> wrapper = new QueryWrapper<>();
        String status = (String) params.get("status");
        String wareId = (String) params.get("wareId");
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(status)){
            wrapper.eq("sku_id",status);
        }
        if(!StringUtils.isEmpty(wareId)){
            wrapper.eq("ware_id",wareId);
        }
        if(!StringUtils.isEmpty(key)){
            wrapper.and(w->{w.eq("id",key).or().eq("purchase_id",key).or().eq("sku_id",key);});
        }
        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

}