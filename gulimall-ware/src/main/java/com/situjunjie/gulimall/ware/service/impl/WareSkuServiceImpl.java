package com.situjunjie.gulimall.ware.service.impl;

import com.situjunjie.common.to.SkuHasStock;
import com.situjunjie.gulimall.ware.exception.NoStockException;
import com.situjunjie.gulimall.ware.vo.LockOrderStockVo;
import com.situjunjie.gulimall.ware.vo.OrderItemVo;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    WareSkuDao wareSkuDao;

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
            skuHasStock.setHasStock(stock==null?false:stock>0);
            return skuHasStock;
        }).collect(Collectors.toList());

        return collect;
    }

    @Override
    @Transactional
    public void lockOrderStock(LockOrderStockVo vo) {

        List<OrderItemVo> orderItems = vo.getItems();
        for (OrderItemVo orderItem : orderItems) {
            //遍历每个订单项
            //1.判断是否有足够的库存
            QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("sku_id",orderItem.getSkuId());
            WareSkuEntity wareSku = getOne(wrapper);
            if(wareSku.getStock()-wareSku.getStockLocked()>=orderItem.getCount()){
                //库存充足,准备进行减库存
                Long count = wareSkuDao.lockSkuStock(wareSku.getSkuId(), orderItem.getCount());
                if(count<1){
                    //扣除库存失败
                    throw new NoStockException(wareSku.getSkuId(),orderItem.getCount(),wareSku.getStock()-wareSku.getStockLocked());
                }
            }else {
                //库存不充足直接抛异常
                throw new NoStockException(wareSku.getSkuId(),orderItem.getCount(),wareSku.getStock()-wareSku.getStockLocked());
            }
        }

    }

}