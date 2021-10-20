package com.situjunjie.gulimall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.situjunjie.gulimall.coupon.entity.SeckillSkuRelationEntity;
import com.situjunjie.gulimall.coupon.service.SeckillSkuRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.situjunjie.common.utils.PageUtils;
import com.situjunjie.common.utils.Query;

import com.situjunjie.gulimall.coupon.dao.SeckillSessionDao;
import com.situjunjie.gulimall.coupon.entity.SeckillSessionEntity;
import com.situjunjie.gulimall.coupon.service.SeckillSessionService;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {

    @Autowired
    SeckillSkuRelationService seckillSkuRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查询最近3天的秒杀活动
     * @return
     */
    @Override
    public List<SeckillSessionEntity> getSeckillSessionLast3Days() {
        QueryWrapper<SeckillSessionEntity> wrapper = new QueryWrapper<SeckillSessionEntity>();
        String begin = LocalDate.now().atTime(LocalTime.MIN).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String end = LocalDate.now().plusDays(3).atTime(LocalTime.MAX).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        wrapper.between("start_time",begin,end);
        List<SeckillSessionEntity> list = this.list(wrapper);
        list.stream().forEach(session->{

            QueryWrapper<SeckillSkuRelationEntity> queryWrapper = new QueryWrapper<SeckillSkuRelationEntity>();
            queryWrapper.eq("promotion_session_id",session.getId());
            List<SeckillSkuRelationEntity> skus = seckillSkuRelationService.list(queryWrapper);
            session.setRelationSkus(skus);
        });
        return list;
    }

}