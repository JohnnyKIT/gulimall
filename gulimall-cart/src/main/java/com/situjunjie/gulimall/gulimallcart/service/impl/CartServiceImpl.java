package com.situjunjie.gulimall.gulimallcart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.situjunjie.common.constant.CartConst;
import com.situjunjie.common.utils.R;
import com.situjunjie.gulimall.gulimallcart.feign.ProductFeignService;
import com.situjunjie.gulimall.gulimallcart.interceptor.CartInterceptor;
import com.situjunjie.gulimall.gulimallcart.service.CartService;
import com.situjunjie.gulimall.gulimallcart.vo.Cart;
import com.situjunjie.gulimall.gulimallcart.vo.CartItem;
import com.situjunjie.gulimall.gulimallcart.vo.SkuInfoVo;
import com.situjunjie.gulimall.gulimallcart.vo.UserInfoTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    ExecutorService executor;
    
    @Override
    public CartItem addProductToCard(String skuId, String num) throws ExecutionException, InterruptedException {
        //获取当前操作用户信息
        BoundHashOperations<String, Object, Object> redisOperation = getCuurentUserRedisOperation();
        String res = (String) redisOperation.get(skuId);
        CartItem cartItem;
        if(StringUtils.isEmpty(res)){
            cartItem = new CartItem();
            //购物车第一次添加这个商品
            CompletableFuture<Void> skuInfoFuture = CompletableFuture.runAsync(() -> {
                //开启异步请求skuInfo
                R r = productFeignService.getSkuInfo(Long.parseLong(skuId));
                if (r.getCode() == 0) {
                    //请求成功返回, 装配cartItem
                    SkuInfoVo skuInfo = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                    });
                    cartItem.setSkuId(Long.parseLong(skuId));
                    cartItem.setTitle(skuInfo.getSkuTitle());
                    cartItem.setImage(skuInfo.getSkuDefaultImg());
                    cartItem.setCount(Integer.parseInt(num));
                    cartItem.setPrice(skuInfo.getPrice());
                }
            }, executor);

            CompletableFuture<Void> saleAttrValueFuture = CompletableFuture.runAsync(() -> {
                //开启异步请求销售属性，装配cartitem
                R r = productFeignService.getSaleAttrValueAsStringList(skuId);
                if (r.getCode() == 0) {
                    //远程调用成功取出数据
                    List<String> saleAttrValue = r.getData("saleAttrValue", new TypeReference<List<String>>() {
                    });
                    cartItem.setSkuAttr(saleAttrValue);
                }
            }, executor);
            //阻塞等待异步任务全部完成
            CompletableFuture.allOf(skuInfoFuture,saleAttrValueFuture).get();

        }else{
            //购物车已经有过这个商品了,数量上增加即可
            cartItem = JSON.parseObject(res, CartItem.class);
            cartItem.setCount(cartItem.getCount()+Integer.parseInt(num));
        }
        //存入到Redis缓存
        redisOperation.put(skuId, JSON.toJSONString(cartItem));

        return cartItem;
    }

    /**
     * 根据skuId获取当前用户的购物项
     * @param skuId
     * @return
     */
    @Override
    public CartItem getCartItemBySkuId(String skuId) {
        BoundHashOperations<String, Object, Object> cuurentUserRedisOperation = getCuurentUserRedisOperation();
        String str = (String) cuurentUserRedisOperation.get(skuId);
        CartItem cartItem = JSON.parseObject(str, CartItem.class);
        return cartItem;
    }


    /**
     *
     * 获取当前用户的操作redis的对象
     * @return
     */
    private BoundHashOperations<String, Object, Object> getCuurentUserRedisOperation() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String redisCartKey;
        if(StringUtils.isEmpty(userInfoTo.getUsername())){
            //用户没有登录
            redisCartKey = CartConst.USER_CART_REDIS_PREFIX+userInfoTo.getUserKey();
        }else{
            redisCartKey = CartConst.USER_CART_REDIS_PREFIX+userInfoTo.getUserId();
        }
        BoundHashOperations<String, Object, Object> stringObjectObjectBoundHashOperations = redisTemplate.boundHashOps(redisCartKey);
        return stringObjectObjectBoundHashOperations;
    }
}
