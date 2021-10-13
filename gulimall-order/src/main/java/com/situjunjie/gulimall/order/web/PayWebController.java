package com.situjunjie.gulimall.order.web;

import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.situjunjie.gulimall.order.config.AliPayTemplate;
import com.situjunjie.gulimall.order.entity.OrderEntity;
import com.situjunjie.gulimall.order.service.OrderService;
import com.situjunjie.gulimall.order.vo.AliPayReqVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;

/**
 * 支付结算相关页面Controller
 */
@Controller
@Slf4j
public class PayWebController {

    @Autowired
    OrderService orderService;

    @Autowired
    AliPayTemplate aliPayTemplate;

    /**
     * 跳转使用支付宝结算
     * @return
     */
    @RequestMapping(value = "/toAliPayPage",produces = "text/html")
    @ResponseBody
    public String toAliPay(@RequestParam("orderSn")String orderSn){
        OrderEntity order = orderService.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        AliPayReqVo aliPayReqVo = new AliPayReqVo();
        aliPayReqVo.setTotal_amount(order.getTotalAmount().setScale(2, BigDecimal.ROUND_UP).toString());
        aliPayReqVo.setSubject("谷粒商城");
        aliPayReqVo.setOutTradeNo(orderSn);
        try {
            String aliPayPage = aliPayTemplate.getAliPayPage(aliPayReqVo);
            //获取到的页面
            log.info("获取到的页面:{}",aliPayPage);
            return aliPayPage;
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return "fail";

    }
}
