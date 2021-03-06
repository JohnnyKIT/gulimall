package com.situjunjie.gulimall.product.service.impl;

import com.situjunjie.gulimall.product.vo.AttrRelationVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.situjunjie.common.utils.PageUtils;
import com.situjunjie.common.utils.Query;

import com.situjunjie.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.situjunjie.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.situjunjie.gulimall.product.service.AttrAttrgroupRelationService;


@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrAttrgroupRelationEntity> page = this.page(
                new Query<AttrAttrgroupRelationEntity>().getPage(params),
                new QueryWrapper<AttrAttrgroupRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void deleteRelationBatch(List<AttrRelationVo> vos) {
        List<AttrAttrgroupRelationEntity> relationList = vos.stream().map(vo -> {
            AttrAttrgroupRelationEntity entity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(vo, entity);
            return entity;
        }).collect(Collectors.toList());
        baseMapper.deleteBatchRelation(relationList);
    }

    @Override
    public void relateAttrAndAttrGroup(List<AttrRelationVo> vos) {

        List<AttrAttrgroupRelationEntity> list = vos.stream().map(attrRelate -> {
            AttrAttrgroupRelationEntity entity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(attrRelate, entity);
            return entity;
        }).collect(Collectors.toList());
        this.saveBatch(list);

    }

}