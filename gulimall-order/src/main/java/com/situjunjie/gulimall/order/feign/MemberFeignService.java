package com.situjunjie.gulimall.order.feign;

import com.situjunjie.common.utils.R;
import com.situjunjie.gulimall.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.List;

@FeignClient("gulimall-member")
public interface MemberFeignService {

    @ResponseBody
    @GetMapping("member/memberreceiveaddress/{memberId}")
    R getMemberReceiveAddress(@PathVariable("memberId") Long id);

    @ResponseBody
    @GetMapping("member/memberreceiveaddress/calFare/{addrId}")
    R calFareByAddrId(@PathVariable("addrId") Long addrId);
}
