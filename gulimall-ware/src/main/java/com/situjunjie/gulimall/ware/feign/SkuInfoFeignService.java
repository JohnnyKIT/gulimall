package com.situjunjie.gulimall.ware.feign;

import com.situjunjie.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-product")
public interface SkuInfoFeignService {

    @RequestMapping("/product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);
}
