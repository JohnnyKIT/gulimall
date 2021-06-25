package com.situjunjie.gulimall.product.controller;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.situjunjie.common.constant.ProductConst;
import com.situjunjie.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.situjunjie.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.situjunjie.gulimall.product.service.AttrAttrgroupRelationService;
import com.situjunjie.gulimall.product.vo.AttrRespVo;
import com.situjunjie.gulimall.product.vo.AttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.situjunjie.gulimall.product.entity.AttrEntity;
import com.situjunjie.gulimall.product.service.AttrService;
import com.situjunjie.common.utils.PageUtils;
import com.situjunjie.common.utils.R;



/**
 * 商品属性
 *
 * @author situjunjie
 * @email situjunjie@vip.qq.com
 * @date 2021-06-16 13:30:30
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    /**
     * 列表
     */
    @RequestMapping("/{type}/list/{categoryId}")
   // @RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params,@PathVariable("categoryId")Long catId,@PathVariable("type")String type){
        PageUtils page = attrService.queryPage(params,catId,type);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
   // @RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
		//AttrEntity attr = attrService.getById(attrId);
		AttrRespVo attr = attrService.getAttrInfo(attrId);

        return R.ok().put("attr", attr);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @Transactional
   // @RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attr){

        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
        attrService.save(attrEntity);
        attr.setAttrId(attrEntity.getAttrId());

        if(attr.getAttrType()== ProductConst.AttrEnum.ATTR_TYPE_BASE.getCode()&&attr.getAttrGroupId()!=null) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(attr, relationEntity);

            attrAttrgroupRelationService.save(relationEntity);
        }

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVo attr){
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
		attrService.updateById(attrEntity);

        attr.setAttrId(attrEntity.getAttrId());

        if(attr.getAttrType()== ProductConst.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(attr, relationEntity);

            attrAttrgroupRelationService.updateById(relationEntity);
        }
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
