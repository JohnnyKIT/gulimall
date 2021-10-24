package com.situjunjie.gulimall.product.web;

import com.alibaba.fastjson.TypeReference;
import com.situjunjie.common.utils.R;
import com.situjunjie.gulimall.product.feign.SeckillInfoFeinService;
import com.situjunjie.gulimall.product.service.SkuInfoService;
import com.situjunjie.gulimall.product.vo.SeckillSkuRedisVo;
import com.situjunjie.gulimall.product.vo.SkuItemVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

@Slf4j
@Controller
public class ItemController {

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SeckillInfoFeinService seckillInfoFeinService;

    @GetMapping("/{skuId}.html")
    public String dispatchertToSkuPage(@PathVariable("skuId")Long skuId, Model model) throws ExecutionException, InterruptedException {
        log.info("准备查询商品详情数据展示");
        SkuItemVo vo = skuInfoService.querySkuItemVoInfo(skuId);
        log.info("查询到商品详情：{}",vo);
        log.info("查询商品秒杀信息");
        R r = seckillInfoFeinService.getSkuSeckillInfoBySkuId(skuId);
        if(r.getCode()==0){
            SeckillSkuRedisVo seckillInfo = r.getData(new TypeReference<SeckillSkuRedisVo>() {
            });
            model.addAttribute("seckillInfo",seckillInfo);
        }
        model.addAttribute("item",vo);
        return "item";
    }
}
