package com.situjunjie.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.situjunjie.gulimall.product.entity.BrandEntity;
import com.situjunjie.gulimall.product.entity.CategoryEntity;
import com.situjunjie.gulimall.product.service.BrandService;
import com.situjunjie.gulimall.product.service.CategoryService;
import com.situjunjie.gulimall.product.vo.BrandVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.web.bind.annotation.*;

import com.situjunjie.gulimall.product.entity.CategoryBrandRelationEntity;
import com.situjunjie.gulimall.product.service.CategoryBrandRelationService;
import com.situjunjie.common.utils.PageUtils;
import com.situjunjie.common.utils.R;



/**
 * 品牌分类关联
 *
 * @author situjunjie
 * @email situjunjie@vip.qq.com
 * @date 2021-06-16 13:30:29
 */
@Slf4j
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;



    /**
     * 获取品牌分类关联
     */
    @GetMapping("/catelog/list")
    // @RequiresPermissions("product:categorybrandrelation:list")
    public R listRelation(@RequestParam Map<String, Object> params){
        List<CategoryBrandRelationEntity> list = categoryBrandRelationService.queryByBrandId(params);

        return R.ok().put("data", list);
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("product:categorybrandrelation:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
   // @RequiresPermissions("product:categorybrandrelation:info")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("product:categorybrandrelation:save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
        CategoryEntity category = categoryService.getById(categoryBrandRelation.getCatelogId());
        BrandEntity brand = brandService.getById(categoryBrandRelation.getBrandId());
        categoryBrandRelation.setBrandName(brand.getName());
        categoryBrandRelation.setCatelogName(category.getName());
		categoryBrandRelationService.save(categoryBrandRelation);


        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:categorybrandrelation:update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:categorybrandrelation:delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * /product/categorybrandrelation/brands/list
     * 获取分类关联的品牌
     */
    @GetMapping("/brands/list")
    public R getRelatedBrandList(@RequestParam(required = true,value = "catId") Long catId){

        log.info("开始执行getRelatedBrandList");

        List<BrandEntity> list = categoryBrandRelationService.listRelatedBrandByCateId(catId);

        List<BrandVo> collect = list.stream().map(brand -> {
            BrandVo brandVo = new BrandVo();
            brandVo.setBrandId(brand.getBrandId());
            brandVo.setBrandName(brand.getName());
            return brandVo;
        }).collect(Collectors.toList());

        return R.ok().put("data",collect);
    }

}
