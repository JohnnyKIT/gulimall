package com.situjunjie.common.to;

import lombok.Data;

import java.io.Serializable;

@Data
public class SkuHasStock implements Serializable {

    private Long skuId;

    private Boolean hasStock;
}
