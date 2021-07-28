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

import java.util.LinkedHashMap;
import java.util.List;

@Controller
public class OrderWebController {

    @Autowired
    OrderService orderService;

    @Autowired
    CartFeignService cartFeignService;

    @Autowired
    MemberFeignService memberFeignService;

    @GetMapping("/toTrade")
    public String toTrade(Model model){
        //0.准备对象
        OrderConfirmVo vo = new OrderConfirmVo();
        MemberEntity memberEntity = LoginUserInterceptor.threadLocal.get();
        //1.获取当前购物车所有选中的购物项
        R cartItemsR = cartFeignService.getCartItemsChecked(memberEntity.getId());
        R addressR = memberFeignService.getMemberReceiveAddress(memberEntity.getId());
        if(cartItemsR.getCode()==0){
            //请求成功获取数据并装配
            List<OrderItemVo> cartItems = cartItemsR.getData("cartItems", new TypeReference<List<OrderItemVo>>() {});
            vo.setItems(cartItems);
        }
        if(addressR.getCode()==0){
            //请求成功获取数据并装配
            List<MemberAddressVo> address = addressR.getData("address", new TypeReference<List<MemberAddressVo>>() {});
            vo.setMemberAddressVos(address);
        }
        vo.setIntegration(memberEntity.getIntegration());
        model.addAttribute("orderConfirmVo",vo);

        return "confirm";
    }
}
