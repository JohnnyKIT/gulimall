package com.situjunjie.gulimall.gulimallcart.controller;

import com.situjunjie.gulimall.gulimallcart.interceptor.CartInterceptor;
import com.situjunjie.gulimall.gulimallcart.service.CartService;
import com.situjunjie.gulimall.gulimallcart.vo.CartItem;
import com.situjunjie.gulimall.gulimallcart.vo.UserInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ExecutionException;

@Slf4j
@Controller
public class CartController {

    @Autowired
    CartService cartService;
    /**
     * 跳转到购物车页面
     */
    @GetMapping("/cart.html")
    public String goCartPage(){
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        log.debug("userInfo = {}",userInfoTo);
        return "cartList";
    }

    @GetMapping("/addToCard")
    public String addToCard(@RequestParam("skuId")String skuId,
                            @RequestParam("num")String num, RedirectAttributes redirectAttributes) throws ExecutionException, InterruptedException {

        CartItem cartItem = cartService.addProductToCard(skuId,num);
        redirectAttributes.addAttribute("skuId",cartItem.getSkuId());
        log.debug("成功加入购物车:{}",cartItem);
        return "redirect:http://cart.gulimall.com/addToCardSuccess";
    }

    @GetMapping("/addToCardSuccess")
    public String addToCardSuccess(@RequestParam("skuId")String skuId,Model model){

        CartItem cartItem = cartService.getCartItemBySkuId(skuId);
        model.addAttribute("cartItem",cartItem);
        return "success";
    }
}