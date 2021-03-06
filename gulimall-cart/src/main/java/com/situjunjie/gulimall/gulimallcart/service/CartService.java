package com.situjunjie.gulimall.gulimallcart.service;


import com.situjunjie.gulimall.gulimallcart.vo.Cart;
import com.situjunjie.gulimall.gulimallcart.vo.CartItem;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface CartService {
    CartItem addProductToCard(String skuId, String num) throws ExecutionException, InterruptedException;

    CartItem getCartItemBySkuId(String skuId);

    Cart getCurrentUserCart();

    void checkCartItem(String skuId, Integer checked);

    void changeItemCount(String skuId, Integer count);

    void deleteCartItem(String skuId);

    List<CartItem> getCartItemsChecked(Long id);
}
