package com.situjunjie.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.situjunjie.gulimall.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.situjunjie.gulimall.product.entity.CategoryEntity;
import com.situjunjie.gulimall.product.service.CategoryService;
import com.situjunjie.common.utils.PageUtils;
import com.situjunjie.common.utils.R;



/**
 * 商品三级分类
 *
 * @author situjunjie
 * @email situjunjie@vip.qq.com
 * @date 2021-06-16 13:30:30
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;


    /**
     * 树形分级查询出所有分类
     */
    @RequestMapping("/list/tree")
    // @RequiresPermissions("product:category:list")
    public R listWithTree(){

        List<CategoryEntity> list = categoryService.listWithTree();

        return R.ok().put("category", list);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("product:category:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
   // @RequiresPermissions("product:category:info")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("category", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("product:category:save")
    public R save(@RequestBody CategoryEntity category){
        System.out.println("category"+category);
		categoryService.save(category);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:category:update")
    public R update(@RequestBody CategoryEntity category){
		categoryService.updateById(category);

        return R.ok();
    }

    /**
     * 修改及其冗余字段
     */
    @Transactional
    @RequestMapping("/update-detail")
    //@RequiresPermissions("product:category:update")
    public R updateDetail(@RequestBody CategoryEntity category){
        categoryService.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());
        return R.ok();
    }

    /**
     * 批量修改
     */
    @RequestMapping("/batch/update")
    //@RequiresPermissions("product:category:update")
    public R updateBatch(@RequestBody CategoryEntity[] category){
        categoryService.updateBatchById(Arrays.asList(category));
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:category:delete")
    public R delete(@RequestBody Long[] catIds){
		categoryService.removeByIds(Arrays.asList(catIds));

        return R.ok();
    }



}
