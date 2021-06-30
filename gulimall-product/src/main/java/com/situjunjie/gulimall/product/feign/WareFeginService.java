package com.situjunjie.gulimall.product.feign;

import com.situjunjie.common.to.SkuHasStock;
import com.situjunjie.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("gulimall-ware")
public interface WareFeginService {

    @RequestMapping("/ware/waresku/hasStock")
    R<List<SkuHasStock>> skuHasStock(List<Long> skuIds);
}
