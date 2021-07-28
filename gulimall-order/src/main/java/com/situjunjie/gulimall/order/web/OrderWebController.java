package com.situjunjie.gulimall.order.web;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.situjunjie.common.to.MemberEntity;
import com.situjunjie.common.utils.R;
import com.situjunjie.gulimall.order.feign.CartFeignService;
import com.situjunjie.gulimall.order.feign.MemberFeignService;
import com.situjunjie.gulimall.order.inteceptor.LoginUserInterceptor;
import com.situjunjie.gulimall.order.service.OrderService;
import com.situjunjie.gulimall.order.vo.MemberAddressVo;
import com.situjunjie.gulimall.order.vo.OrderConfirmVo;
import com.situjunjie.gulimall.order.vo.OrderItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

@Controller
public class OrderWebController {

    @Autowired
    OrderService orderService;

    @Autowired
    CartFeignService cartFeignService;

    @Autowired
    MemberFeignService memberFeignService;

    @Autowired
    ExecutorService executor;

    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        //0.准备对象
        OrderConfirmVo vo = new OrderConfirmVo();
        MemberEntity memberEntity = LoginUserInterceptor.threadLocal.get();
        //1.获取当前购物车所有选中的购物项
        //开启异步  需要注意！！！ 加入了Feign的拦截器 ,需要同步不同线程的requestAttribute
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        CompletableFuture<Void> cartItemsFuture = CompletableFuture.runAsync(()->{
            RequestContextHolder.setRequestAttributes(requestAttributes);
            R cartItemsR = cartFeignService.getCartItemsChecked(memberEntity.getId());
            if(cartItemsR.getCode()==0){
                //请求成功获取数据并装配
                List<OrderItemVo> cartItems = cartItemsR.getData("cartItems", new TypeReference<List<OrderItemVo>>() {});
                vo.setItems(cartItems);
            }
        },executor);
        CompletableFuture<Void> addressFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            R addressR = memberFeignService.getMemberReceiveAddress(memberEntity.getId());
            if (addressR.getCode() == 0) {
                //请求成功获取数据并装配
                List<MemberAddressVo> address = addressR.getData("address", new TypeReference<List<MemberAddressVo>>() {
                });
                vo.setMemberAddressVos(address);
            }
        }, executor);
        CompletableFuture.allOf(cartItemsFuture,addressFuture).get();
        vo.setIntegration(memberEntity.getIntegration());
        model.addAttribute("orderConfirmVo",vo);

        return "confirm";
    }
}
