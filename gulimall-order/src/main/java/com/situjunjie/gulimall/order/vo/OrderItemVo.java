package com.situjunjie.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderItemVo {
    private Long skuId;



    private String title;
    private String image;

    private List<String> skuAttr;

    /*** 价格*/
    private BigDecimal price;
    /*** 数量*/
    private Integer count;

    private BigDecimal totalPrice;
}
