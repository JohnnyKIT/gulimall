package com.situjunjie.gulimall.order.feign;

import com.situjunjie.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("gulimall-ware")
public interface WareFeignService {

    @RequestMapping("ware/waresku/hasStock")
     R skuHasStock(@RequestBody List<Long> skuIds);

}
