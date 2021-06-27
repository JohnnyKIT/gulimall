package com.situjunjie.gulimall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.situjunjie.gulimall.ware.vo.MergeVo;
import com.situjunjie.gulimall.ware.vo.PurchaseDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.situjunjie.gulimall.ware.entity.PurchaseEntity;
import com.situjunjie.gulimall.ware.service.PurchaseService;
import com.situjunjie.common.utils.PageUtils;
import com.situjunjie.common.utils.R;



/**
 * 采购信息
 *
 * @author situjunjie
 * @email situjunjie@vip.qq.com
 * @date 2021-06-16 13:33:09
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;


    /**
     *
     * /ware/purchase/done
     */
    @PostMapping("done")
    public R purchaseDone(@RequestBody PurchaseDoneVo doneVo){
        purchaseService.done(doneVo);
        return R.ok();
    }


    /**
     * /ware/purchase/received
     * @param
     * @return
     */

    @PostMapping("/received")
    public R received(@RequestBody List<Long> ids){
        purchaseService.received(ids);
        return  R.ok();
    }

    @PostMapping("/merge")
    public R merge(@RequestBody MergeVo mergeVo){
        purchaseService.mergePurchaseDetail(mergeVo);
        return R.ok();
    }

    /**
     * /ware/purchase/unreceive/list
     */
    @RequestMapping("/unreceive/list")
    // @RequiresPermissions("ware:purchase:list")
    public R listUnreceive(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPageUnrecive(params);

        return R.ok().put("page", page);
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("ware:purchase:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
   // @RequiresPermissions("ware:purchase:info")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("ware:purchase:save")
    public R save(@RequestBody PurchaseEntity purchase){
		purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:purchase:update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:purchase:delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
