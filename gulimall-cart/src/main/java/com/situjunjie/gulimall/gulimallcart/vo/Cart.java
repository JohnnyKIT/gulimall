package com.situjunjie.gulimall.gulimallcart.vo;

import java.math.BigDecimal;
import java.util.List;

public class Cart {

    private List<CartItem> items;

    /*** 商品的数量*/
    private Integer countNum;

    /*** 商品的类型数量*/
    private Integer countType;

    /*** 整个购物车的总价*/
    private BigDecimal totalAmount;

    /*** 减免的价格*/
    private BigDecimal reduce = new BigDecimal("0.00");

    /*** 计算商品的总量*/
    public Integer getCountNum() {
        int count = 0;
        if (this.items != null && this.items.size() > 0) {
            for (CartItem item : this.items) {
                count += item.getCount();
            }
        }
        return count;
    }

    public Integer getCountType() {
        int count = 0;
        if (this.items != null && this.items.size() > 0) {
            for (CartItem item : this.items) {
                count += 1;
            }
        }
        return count;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal amount = new BigDecimal("0");
        if (this.items != null && this.items.size() > 0) {
            for (CartItem item : this.items) {
                if (item.getCheck()) {
                    BigDecimal totalPrice = item.getTotalPrice();
                    amount = amount.add(totalPrice);
                }
            }
        }
        return amount.subtract(this.getReduce());
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public void setCountNum(Integer countNum) {
        this.countNum = countNum;
    }

    public void setCountType(Integer countType) {
        this.countType = countType;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "items=" + items +
                ", countNum=" + getCountNum() +
                ", countType=" + getCountType() +
                ", totalAmount=" + getTotalAmount() +
                ", reduce=" + getReduce() +
                '}';
    }
}

