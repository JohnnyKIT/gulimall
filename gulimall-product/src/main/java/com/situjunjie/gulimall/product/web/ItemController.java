package com.situjunjie.gulimall.product.web;

import com.situjunjie.gulimall.product.service.SkuInfoService;
import com.situjunjie.gulimall.product.vo.SkuItemVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller
public class ItemController {

    @Autowired
    SkuInfoService skuInfoService;

    @GetMapping("/{skuId}.html")
    public String dispatchertToSkuPage(@PathVariable("skuId")Long skuId, Model model){
        log.info("准备查询商品详情数据展示");
        SkuItemVo vo = skuInfoService.querySkuItemVoInfo(skuId);
        log.info("查询到商品详情：{}",vo);
        model.addAttribute("item",vo);
        return "item";
    }
}
