package com.situjunjie.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.situjunjie.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.situjunjie.gulimall.product.entity.AttrEntity;
import com.situjunjie.gulimall.product.service.AttrAttrgroupRelationService;
import com.situjunjie.gulimall.product.service.AttrService;
import com.situjunjie.gulimall.product.service.CategoryService;
import com.situjunjie.gulimall.product.vo.AttrGroupAttrsVo;
import com.situjunjie.gulimall.product.vo.AttrRelationVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.situjunjie.gulimall.product.entity.AttrGroupEntity;
import com.situjunjie.gulimall.product.service.AttrGroupService;
import com.situjunjie.common.utils.PageUtils;
import com.situjunjie.common.utils.R;



/**
 * 属性分组
 *
 * @author situjunjie
 * @email situjunjie@vip.qq.com
 * @date 2021-06-16 13:30:29
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrGroupService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 按照三级分类ID查询列表
     */
    @RequestMapping("/list/{catelogId}")
    // @RequiresPermissions("product:attrgroup:list")
    public R listByCategory(@RequestParam Map<String, Object> params,@PathVariable("catelogId") Long categoryId){
        PageUtils page = attrGroupService.queryPage(params,categoryId);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
   // @RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

        Long catelogId = attrGroup.getCatelogId();
        Long[] path = categoryService.getCatelogPath(catelogId);

        attrGroup.setCatelogPath(path);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

//    this.attrGroupId + "/attr/relation"
    /**
     * 查询关联关系
     */
    @RequestMapping("/{attrGroupId}/attr/relation")
    public R getRelation(@PathVariable("attrGroupId")Long attrGroupId){

        List<AttrEntity> list = attrService.getAttrRelation(attrGroupId);
        return R.ok().put("data",list);
    }

    /**
     * 批量删除关联关系
     */
    @RequestMapping("/attr/relation/delete")
    public R deleteRelationBatch(@RequestBody List<AttrRelationVo> vos){

        attrAttrgroupRelationService.deleteRelationBatch(vos);
        return R.ok();
    }

    /**
     * 查询可关联的属性列表
     */
    @RequestMapping("/{attrGroupId}/noattr/relation")
    public R getRelatableAttr(@PathVariable("attrGroupId")Long attrGroupId,@RequestParam Map<String,Object> params){

        PageUtils page = attrService.getRelatableAttr(attrGroupId,params);
        return R.ok().put("page",page);
    }
    /**
     *
     * 添加分组与属性的关联关系
     * /product/attrgroup/attr/relation
     */
    @RequestMapping("/attr/relation")
    public R relateAttrAndAttrGroup(@RequestBody List<AttrRelationVo> vos){
        attrAttrgroupRelationService.relateAttrAndAttrGroup(vos);
        return R.ok();
    }

    /**
     * /product/attrgroup/{catelogId}/withattr
     * 获取分类下所有分组&关联属性
     */
    @GetMapping("/{catelogId}/withattr")
    public R getAttrGroupWithAttrByCatId(@PathVariable("catelogId")Long catId){

        //List<AttrAttrgroupRelationEntity> relationList = attrAttrgroupRelationService.list(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("catelog_id", catId));
        List<AttrGroupEntity> attrGroupList = attrGroupService.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catId));
        List<AttrGroupAttrsVo> volist = attrGroupList.stream().map(relation -> {
            AttrGroupEntity attrGroup = attrGroupService.getById(relation.getAttrGroupId());
            AttrGroupAttrsVo vo = new AttrGroupAttrsVo();
            BeanUtils.copyProperties(attrGroup,vo);
            List<AttrEntity> attrs = attrService.getAttrRelation(attrGroup.getAttrGroupId());
            vo.setAttrs(attrs);
            return  vo;
        }).collect(Collectors.toList());


        return R.ok().put("data",volist);
    }

}
