package com.situjunjie.gulimall.product.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SkuItemSaleAttrVo {
    private Long attrId;
    private String attrName;
    private List<SpuItemSaleAttrValues> attrValues = new ArrayList<>();
}
