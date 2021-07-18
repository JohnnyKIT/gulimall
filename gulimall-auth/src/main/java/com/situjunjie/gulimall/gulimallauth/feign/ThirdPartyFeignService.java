package com.situjunjie.gulimall.gulimallauth.feign;

import com.situjunjie.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("gulimall-third-party")
public interface ThirdPartyFeignService {

    @RequestMapping("/sms/sendCode")
    R sendCode(@RequestParam("phoneNum")String phoneNum, @RequestParam("code")String code);
}
