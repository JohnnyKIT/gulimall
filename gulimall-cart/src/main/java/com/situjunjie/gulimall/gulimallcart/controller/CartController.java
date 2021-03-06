package com.situjunjie.gulimall.gulimallcart.controller;

import com.situjunjie.common.utils.R;
import com.situjunjie.gulimall.gulimallcart.interceptor.CartInterceptor;
import com.situjunjie.gulimall.gulimallcart.service.CartService;
import com.situjunjie.gulimall.gulimallcart.vo.Cart;
import com.situjunjie.gulimall.gulimallcart.vo.CartItem;
import com.situjunjie.gulimall.gulimallcart.vo.UserInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
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
    public String goCartPage(Model model){
        Cart cart = cartService.getCurrentUserCart();
        model.addAttribute("cart",cart);
        log.info("获取到的购物车对象={}",cart);
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

    /**
     * 更改购物项check状态
     */
    @GetMapping("/checkItem")
    public String checkItem(@RequestParam("skuId")String skuId,
                            @RequestParam("checked")Integer checked){
        cartService.checkCartItem(skuId,checked);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    /**
     * 更改购物项的数量
     */
    @GetMapping("/changeItemCount")
    public String changeItemCount(@RequestParam("skuId")String skuId,
                                  @RequestParam("count")Integer count){
        cartService.changeItemCount(skuId,count);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    /**
     * 删除购物项
     */
    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId")String skuId){
        cartService.deleteCartItem(skuId);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @GetMapping("/cart/{memberId}/cartItems")
    @ResponseBody
    public R getCartItemsChecked(@PathVariable("memberId") Long id){
        List<CartItem> cartItems = cartService.getCartItemsChecked(id);
        return R.ok().put("cartItems",cartItems);
    }
}
