package com.situjunjie.gulimall.ware.service.impl;

import com.situjunjie.common.to.SkuHasStock;
import com.situjunjie.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.situjunjie.gulimall.ware.entity.WareOrderTaskEntity;
import com.situjunjie.gulimall.ware.enums.OrderTaskStatusEmun;
import com.situjunjie.gulimall.ware.exception.NoStockException;
import com.situjunjie.gulimall.ware.service.WareOrderTaskDetailService;
import com.situjunjie.gulimall.ware.service.WareOrderTaskService;
import com.situjunjie.gulimall.ware.vo.LockOrderStockVo;
import com.situjunjie.gulimall.ware.vo.OrderItemVo;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

import com.situjunjie.gulimall.ware.dao.WareSkuDao;
import com.situjunjie.gulimall.ware.entity.WareSkuEntity;
import com.situjunjie.gulimall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    WareSkuDao wareSkuDao;

    @Autowired
    WareOrderTaskService wareOrderTaskService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    WareOrderTaskDetailService wareOrderTaskDetailService;

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
        //???????????????????????????????????????????????????
        createOrderLockTask(vo);
        for (OrderItemVo orderItem : orderItems) {
            //?????????????????????
            //1.??????????????????????????????
            QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("sku_id",orderItem.getSkuId());
            WareSkuEntity wareSku = getOne(wrapper);
            if(wareSku.getStock()-wareSku.getStockLocked()>=orderItem.getCount()){
                //????????????,?????????????????????
                Long count = wareSkuDao.lockSkuStock(wareSku.getSkuId(), orderItem.getCount());
                //????????????????????????
                WareOrderTaskDetailEntity taskDetail = new WareOrderTaskDetailEntity();
                taskDetail.setSkuId(wareSku.getSkuId());
                taskDetail.setSkuName(wareSku.getSkuName());
                taskDetail.setLockStatus(orderItem.getCount());
                taskDetail.setLockStatus(1); //?????????
                taskDetail.setWareId(wareSku.getWareId());
                taskDetail.setSkuNum(orderItem.getCount());
                wareOrderTaskDetailService.save(taskDetail);
                //?????????????????????
                rabbitTemplate.convertAndSend("stock-event-exchange","stock.locked",taskDetail);
                if(count<1){
                    //??????????????????
                    throw new NoStockException(wareSku.getSkuId(),orderItem.getCount(),wareSku.getStock()-wareSku.getStockLocked());
                }
            }else {
                //??????????????????????????????
                throw new NoStockException(wareSku.getSkuId(),orderItem.getCount(),wareSku.getStock()-wareSku.getStockLocked());
            }
        }

    }

    /**
     * ?????????????????????????????????????????????
     * @param vo
     */
    private void createOrderLockTask(LockOrderStockVo vo) {
        WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
        taskEntity.setOrderSn(vo.getOrderSn());
        taskEntity.setCreateTime(new Date());
        wareOrderTaskService.save(taskEntity);
    }

    /**
     * ??????????????????
     */
    public void releaseStock(WareOrderTaskDetailEntity entity){
        WareOrderTaskDetailEntity orderTask = wareOrderTaskDetailService.getById(entity.getId());
        if(orderTask!=null){
            //?????????????????????????????????
            WareOrderTaskDetailEntity update = new WareOrderTaskDetailEntity();
            update.setId(orderTask.getId());
            update.setLockStatus(OrderTaskStatusEmun.Unlocked.getCode());
            wareOrderTaskDetailService.updateById(update);
            //????????????????????????
            this.baseMapper.unLockOrderStock(entity.getSkuId(),entity.getWareId(),entity.getSkuNum());
        }

    }

}