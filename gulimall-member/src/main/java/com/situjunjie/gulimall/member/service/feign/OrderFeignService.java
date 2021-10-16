package com.situjunjie.gulimall.member.service.feign;

import com.situjunjie.common.to.MemberOrderReqTo;
import com.situjunjie.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 订单服务远程调用客户端
 */
@FeignClient("gulimall-order")
public interface OrderFeignService {

    @PostMapping("/order/order/getMemberOrder")
    R getMemberOrder(@RequestBody MemberOrderReqTo memberOrderReqTo);
}
