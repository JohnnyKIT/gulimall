package com.situjunjie.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.situjunjie.gulimall.product.entity.AttrEntity;
import com.situjunjie.gulimall.product.service.AttrAttrgroupRelationService;
import com.situjunjie.gulimall.product.service.AttrService;
import com.situjunjie.gulimall.product.service.CategoryService;
import com.situjunjie.gulimall.product.vo.AttrRelationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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


}
