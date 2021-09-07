package com.situjunjie.gulimall.ware.exception;

import com.situjunjie.common.exception.BizCodeEnum;

public class NoStockException extends RuntimeException{

    private Long skuId;
    private Integer require;
    private Integer currentStock;

    public NoStockException(Long skuId, Integer require, Integer currentStock) {
        super(BizCodeEnum.NO_STOCK_EXCEPTION.getMessage()+"商品id = "+skuId+" 需求量 = "+require+" 现有库存量 ="+currentStock);
        this.skuId = skuId;
        this.require = require;
        this.currentStock = currentStock;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Integer getRequire() {
        return require;
    }

    public void setRequire(Integer require) {
        this.require = require;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }
}
