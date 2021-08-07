package com.situjunjie.gulimall.order.vo;

import com.situjunjie.gulimall.order.entity.OrderEntity;
import lombok.Data;

@Data
public class OrderSubmitResponseVo {

    private OrderEntity orderEntity;

    /*
    0:成功 1:失败
     */
    private Integer code;
}
