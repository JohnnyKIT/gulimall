package com.situjunjie.gulimall.gulimallcart.controller;

import com.situjunjie.gulimall.gulimallcart.interceptor.CartInterceptor;
import com.situjunjie.gulimall.gulimallcart.vo.UserInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class CartController {

    /**
     * 跳转到购物车页面
     */
    @GetMapping("/cart.html")
    public String goCartPage(){
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        log.debug("userInfo = {}",userInfoTo);
        return "cartList";
    }
}
