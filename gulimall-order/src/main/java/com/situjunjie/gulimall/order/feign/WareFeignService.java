package com.situjunjie.gulimall.order.feign;

import com.situjunjie.common.utils.R;
import com.situjunjie.gulimall.order.vo.LockOrderStockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("gulimall-ware")
public interface WareFeignService {

    @RequestMapping("ware/waresku/hasStock")
     R skuHasStock(@RequestBody List<Long> skuIds);

    @PostMapping("ware/waresku/lock/order")
    R lockStock(@RequestBody LockOrderStockVo vo);



}
