package com.situjunjie.gulimall.ware.service.impl;

import com.situjunjie.common.constant.WareConst;
import com.situjunjie.common.to.SkuInfoEntity;
import com.situjunjie.common.to.SkuInfoTo;
import com.situjunjie.common.utils.R;
import com.situjunjie.gulimall.ware.entity.PurchaseDetailEntity;
import com.situjunjie.gulimall.ware.entity.WareSkuEntity;
import com.situjunjie.gulimall.ware.feign.SkuInfoFeignService;
import com.situjunjie.gulimall.ware.service.PurchaseDetailService;
import com.situjunjie.gulimall.ware.service.WareSkuService;
import com.situjunjie.gulimall.ware.vo.MergeVo;
import com.situjunjie.gulimall.ware.vo.PurchaseDoneVo;
import com.situjunjie.gulimall.ware.vo.PurchaseItemDoneVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.situjunjie.common.utils.PageUtils;
import com.situjunjie.common.utils.Query;

import com.situjunjie.gulimall.ware.dao.PurchaseDao;
import com.situjunjie.gulimall.ware.entity.PurchaseEntity;
import com.situjunjie.gulimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
@Slf4j
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    PurchaseDetailService purchaseDetailService;

    @Autowired
    WareSkuService wareSkuService;

    @Autowired
    SkuInfoFeignService skuInfoFeignService;
    
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnrecive(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status",0).or().eq("status",1)
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void mergePurchaseDetail(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        if(mergeVo.getPurchaseId()==null){
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setStatus(WareConst.PurchaseStatusEnum.CREATED.getCode());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }

        List<Long> items = mergeVo.getItems();
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> collect = items.stream().map(i -> {
            PurchaseDetailEntity entity = new PurchaseDetailEntity();
            entity.setId(i);
            entity.setPurchaseId(finalPurchaseId);
            entity.setStatus(WareConst.PurchaseDetailStatusEnum.ASSIGNED.getCode());
            return entity;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(collect);
        PurchaseEntity p =new PurchaseEntity();
        p.setId(purchaseId);
        p.setUpdateTime(new Date());
        this.updateById(p);


    }

    @Override
    public void received(List<Long> ids) {
        
        //1.只能更新 新建和待分配的采购单
        List<PurchaseEntity> collect = ids.stream().map(id -> {
            PurchaseEntity byId = this.getById(id);
            return byId;
        }).filter(item -> {
            if (item.getStatus() == WareConst.PurchaseStatusEnum.CREATED.getCode() ||
                    item.getStatus() == WareConst.PurchaseStatusEnum.ASSIGNED.getCode()) {
                return true;
            } else {
                return false;
            }
        }).map(item -> {
            PurchaseEntity entity = new PurchaseEntity();
            entity.setId(item.getId());
            entity.setStatus(WareConst.PurchaseStatusEnum.RECEIVE.getCode());
            entity.setUpdateTime(new Date());
            return entity;
        }).collect(Collectors.toList());
        this.updateBatchById(collect);

        collect.forEach(item->{
            List<PurchaseDetailEntity> list = purchaseDetailService.list(new QueryWrapper<PurchaseDetailEntity>().eq("purchase_id", item.getId()));
            List<PurchaseDetailEntity> collect1 = list.stream().map(entity -> {
                PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                purchaseDetailEntity.setId(entity.getId());
                purchaseDetailEntity.setStatus(WareConst.PurchaseDetailStatusEnum.RECEIVE.getCode());

                return purchaseDetailEntity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(collect1);
        });

    }

    @Override
    @Transactional
    public void done(PurchaseDoneVo doneVo) {


        //1.改变采购项状态
        Boolean flag = true;
        List<PurchaseItemDoneVo> items = doneVo.getItems();
        for (PurchaseItemDoneVo item : items) {
            if (item.getStatus()==WareConst.PurchaseDetailStatusEnum.HASERROR.getCode()){
                flag = false;
            }
        }
        List<PurchaseDetailEntity> collect = items.stream().map(item -> {
            PurchaseDetailEntity purchaseDetailEntity = purchaseDetailService.getById(item.getItemId());
            purchaseDetailEntity.setId(item.getItemId());
            purchaseDetailEntity.setStatus(item.getStatus());
            return purchaseDetailEntity;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(collect);

        //2.改变采供单状态

        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(doneVo.getId());
        if(!flag){
            purchaseEntity.setStatus(WareConst.PurchaseStatusEnum.HASERROR.getCode());
        }else{
            purchaseEntity.setStatus(WareConst.PurchaseStatusEnum.FINISH.getCode());
        }
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);

        //3.采购成功的入库
        collect.stream().filter(item->{
            if (item.getStatus()==WareConst.PurchaseDetailStatusEnum.HASERROR.getCode()){
                return false;
            }else{
                return true;
            }
        }).forEach(item -> {
            WareSkuEntity one = wareSkuService.getOne(new QueryWrapper<WareSkuEntity>().eq("sku_id", item.getSkuId()).eq("ware_id",item.getWareId()));
            if(one==null){
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(item.getSkuId());
            wareSkuEntity.setWareId(item.getWareId());
            wareSkuEntity.setStock(item.getSkuNum());

            R info = skuInfoFeignService.info(item.getSkuId());
            if(info.getCode()==0){
                log.info("返回的info={}",info);
                Map<String,Object> map = (Map<String, Object>) info.get("skuInfo");
                wareSkuEntity.setSkuName((String) map.get("skuName"));
            }
            log.info("第一次新增库存信息={}",wareSkuEntity);
            wareSkuService.save(wareSkuEntity);
            }else{
                one.setStock(one.getStock()+item.getSkuNum());
                log.info("更新库存信息={}",one);
                wareSkuService.updateById(one);
            }

        });
    }

}