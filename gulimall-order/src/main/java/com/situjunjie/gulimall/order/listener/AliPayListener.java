package com.situjunjie.gulimall.order.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用于处理支付宝交易的异步回调
 */
@RestController
@Slf4j
public class AliPayListener {


    /**
     * 处理支付成功后的异步回调
     * @return
     */
    @PostMapping("/payed/notify")
    public String payedNotify(){
        log.info("收到支付宝异步回调通知");


        return "success";
    }
}
