package com.situjunjie.gulimall.order.vo;

import lombok.Data;

@Data
public class AliPayReqVo {

    //订单号
    String outTradeNo;

    //付款金额
    String total_amount;

    //标题
    String subject;

    //描述
    String body;
}
