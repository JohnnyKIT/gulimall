package com.situjunjie.gulimall.product.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller
public class ItemController {

    @GetMapping("/{skuId}.html")
    public String dispatchertToSkuPage(@PathVariable("skuId")Long skuId){
        log.info("准备查询商品详情数据展示");
        return "item";
    }
}
