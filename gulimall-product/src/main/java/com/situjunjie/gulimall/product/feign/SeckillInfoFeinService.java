package com.situjunjie.gulimall.product.feign;

import com.situjunjie.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("gulimall-seckill")
public interface SeckillInfoFeinService {

    @GetMapping("/seckill/sku/{skuId}")
    R getSkuSeckillInfoBySkuId(@PathVariable("skuId")Long skuId);
}
