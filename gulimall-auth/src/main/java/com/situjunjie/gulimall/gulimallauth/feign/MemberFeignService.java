package com.situjunjie.gulimall.gulimallauth.feign;

import com.situjunjie.common.utils.R;
import com.situjunjie.gulimall.gulimallauth.vo.UserLoginVo;
import com.situjunjie.gulimall.gulimallauth.vo.UserRegistVo;
import com.situjunjie.gulimall.gulimallauth.vo.WeiboAccessTokenVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-member")
public interface MemberFeignService {

    @RequestMapping("member/member/regist")
     R memberRegist(@RequestBody UserRegistVo vo);

    @PostMapping("member/member/login")
     R memberLogin(@RequestBody UserLoginVo vo);

    @PostMapping("member/member/weibo_login")
    R weiboLogin(@RequestBody WeiboAccessTokenVo weiboAccessTokenVo);
}
