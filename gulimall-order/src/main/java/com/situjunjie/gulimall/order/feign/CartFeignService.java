package com.situjunjie.gulimall.order.feign;

import com.situjunjie.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@FeignClient("gulimall-cart")
public interface CartFeignService {

    @GetMapping("/cart/{memberId}/cartItems")
    @ResponseBody
    R getCartItemsChecked(@PathVariable("memberId") Long id);
}
