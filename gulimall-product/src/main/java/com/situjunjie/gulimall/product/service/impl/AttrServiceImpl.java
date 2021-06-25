package com.situjunjie.gulimall.product.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.situjunjie.common.constant.ProductConst;
import com.situjunjie.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.situjunjie.gulimall.product.dao.AttrGroupDao;
import com.situjunjie.gulimall.product.dao.CategoryDao;
import com.situjunjie.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.situjunjie.gulimall.product.entity.AttrGroupEntity;
import com.situjunjie.gulimall.product.entity.CategoryEntity;
import com.situjunjie.gulimall.product.service.CategoryService;
import com.situjunjie.gulimall.product.vo.AttrRespVo;
import org.springframework.beans.BeanUtils;
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

import com.situjunjie.gulimall.product.dao.AttrDao;
import com.situjunjie.gulimall.product.entity.AttrEntity;
import com.situjunjie.gulimall.product.service.AttrService;
import org.springframework.util.StringUtils;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Autowired
    CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catId,String type) {
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>().eq("attr_type",type.equals("sale")?0:1);
        if(catId!=0L){
            wrapper.eq("catelog_id",catId);
        }
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.eq("attr_id",key).or().like("attr_name",key);
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params),wrapper);

        List<AttrEntity> records = page.getRecords();

        List<AttrRespVo> list = records.stream().map(attrEntity -> {
            Long attrId = attrEntity.getAttrId();
            Long catelogId = attrEntity.getCatelogId();
            AttrRespVo respVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, respVo);
            CategoryEntity category = categoryDao.selectOne(new QueryWrapper<CategoryEntity>().eq("cat_id", respVo.getCatelogId()));
            if (category != null) {
                respVo.setCatelogName(category.getName());
            }
            if(respVo.getAttrType()== ProductConst.AttrEnum.ATTR_TYPE_BASE.getCode()) {
                AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
                if (relationEntity != null) {
                    AttrGroupEntity attrGroup = attrGroupDao.selectOne(new QueryWrapper<AttrGroupEntity>().eq("attr_group_id", relationEntity.getAttrGroupId()));
                    if (attrGroup != null) {
                        respVo.setGroupName(attrGroup.getAttrGroupName());
                    }
                }
            }
            return respVo;
        }).collect(Collectors.toList());


        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setList(list);
        return pageUtils;
    }

    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        AttrRespVo vo = new AttrRespVo();
        AttrEntity attrEntity = baseMapper.selectOne(new QueryWrapper<AttrEntity>().eq("attr_id", attrId));

        BeanUtils.copyProperties(attrEntity,vo);
        CategoryEntity categoryEntity = categoryDao.selectOne(new QueryWrapper<CategoryEntity>().eq("cat_id",attrEntity.getCatelogId()));
        if(categoryEntity!=null){
            vo.setCatelogName(categoryEntity.getName());
        Long[] catelogPath = categoryService.getCatelogPath(categoryEntity.getCatId());
        vo.setCatelogPath(catelogPath);
        }
        if(vo.getAttrType()== ProductConst.AttrEnum.ATTR_TYPE_BASE.getCode()){
        AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
        if(relationEntity!=null){
            AttrGroupEntity attrGroupEntity = attrGroupDao.selectOne(new QueryWrapper<AttrGroupEntity>().eq("attr_group_id", relationEntity.getAttrGroupId()));
            if(attrGroupEntity!=null){
                vo.setGroupName(attrGroupEntity.getAttrGroupName());
                vo.setAttrGroupId(attrGroupEntity.getAttrGroupId()) ;
            }
        }
        }


        return vo;
    }

    @Override
    public List<AttrEntity> getAttrRelation(Long attrGroupId) {
        List<AttrAttrgroupRelationEntity> relationList = attrAttrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrGroupId));
        List<AttrEntity> attrList = relationList.stream().map(relation -> {

            AttrEntity attr = this.getById(relation.getAttrId());
            return attr;
        }).collect(Collectors.toList());

        return attrList;
    }

}