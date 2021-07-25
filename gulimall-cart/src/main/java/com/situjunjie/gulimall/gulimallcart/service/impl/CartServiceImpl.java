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
import java.util.stream.Collectors;

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
     * 获取当前用户的购物车
     * @return
     */
    @Override
    public Cart getCurrentUserCart() {
        //1.从redis中获取当前用户的购物车
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        Cart cart = new Cart();
        if(StringUtils.isEmpty(userInfoTo.getUserId())){
            //用户还没登录,获取临时购物车
            List<CartItem> cartItems = getCartItemsByCartKey(userInfoTo.getUserKey());
            cart.setItems(cartItems);
            return cart;
        }else{
            //用户已经登录
            List<CartItem> cartItems = getCartItemsByCartKey(userInfoTo.getUserId().toString());
            //合并当前临时购物车商品，并清空
            List<CartItem> tempCartItems = getCartItemsByCartKey(userInfoTo.getUserKey());
            //合并购物项到用户购物车
            combineCartItemList(tempCartItems, cartItems);
            //删除临时购物车
            redisTemplate.delete(CartConst.USER_CART_REDIS_PREFIX+userInfoTo.getUserKey());
            //更新购物车到redis
            BoundHashOperations<String, Object, Object> cuurentUserRedisOperation = getCuurentUserRedisOperation();
            cartItems.forEach(item->{
                cuurentUserRedisOperation.put(item.getSkuId().toString(),JSON.toJSONString(item));
            });
            cart.setItems(cartItems);
            return cart;
        }

    }

    /**
     * 更改购物项选中状态
     * @param skuId
     * @param checked
     */
    @Override
    public void checkCartItem(String skuId, Integer checked) {
        BoundHashOperations<String, Object, Object> cuurentUserRedisOperation = getCuurentUserRedisOperation();
        CartItem cartItem = getCartItem(skuId);
        if(checked==1){
            cartItem.setCheck(true);
        }else{
            cartItem.setCheck(false);
        }
        String json = JSON.toJSONString(cartItem);
        cuurentUserRedisOperation.put(skuId,json);
    }

    private CartItem getCartItem(String skuId) {
        BoundHashOperations<String, Object, Object> cuurentUserRedisOperation = getCuurentUserRedisOperation();
        Object obj = cuurentUserRedisOperation.get(skuId);
        CartItem cartItem = JSON.parseObject((String) obj, CartItem.class);
        return cartItem;
    }

    @Override
    public void changeItemCount(String skuId, Integer count) {
        BoundHashOperations<String, Object, Object> cuurentUserRedisOperation = getCuurentUserRedisOperation();
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCount(count);
        String json = JSON.toJSONString(cartItem);
        cuurentUserRedisOperation.put(skuId,json);
    }

    /**
     * 删除购物项
     * @param skuId
     */
    @Override
    public void deleteCartItem(String skuId) {
        BoundHashOperations<String, Object, Object> operation = getCuurentUserRedisOperation();
        operation.delete(skuId);
    }

    /**
     * 合并购物项
     * @param source
     * @param target
     */
    private void combineCartItemList(List<CartItem> source, List<CartItem> target) {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        BoundHashOperations<String, Object, Object> cartOps = redisTemplate.boundHashOps(CartConst.USER_CART_REDIS_PREFIX + userInfoTo.getUserId());
        for (CartItem tempCartItem : source) {
            if(cartOps.get(tempCartItem.getSkuId())==null){
                //用户购物车没有这个临时购物车的购物项,则添加进去
                target.add(tempCartItem);
            }else{
                //用户购物车已存在相同sku商品，则增加数量即可
                for (CartItem cartItem : target) {
                    if(cartItem.getSkuId().equals(tempCartItem.getSkuId())){
                        cartItem.setCount(cartItem.getCount()+tempCartItem.getCount());
                    }
                }
            }
        }
    }


    /**
     * 根据临时或用户Id获取购物车所有购物项
     * @param Cartkey
     * @return
     */
    private List<CartItem> getCartItemsByCartKey(String Cartkey) {

        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(CartConst.USER_CART_REDIS_PREFIX + Cartkey);
        List<Object> values = hashOps.values();
        return values.stream().map(obj -> {
            String json = (String) obj;
            CartItem cartItem = JSON.parseObject(json, CartItem.class);
            return cartItem;
        }).collect(Collectors.toList());
    }


    /**
     *
     * 获取当前用户的操作redis的对象
     * @return
     */
    private BoundHashOperations<String, Object, Object> getCuurentUserRedisOperation() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String redisCartKey;
        if(StringUtils.isEmpty(userInfoTo.getUserId())){
            //用户没有登录
            redisCartKey = CartConst.USER_CART_REDIS_PREFIX+userInfoTo.getUserKey();
        }else{
            redisCartKey = CartConst.USER_CART_REDIS_PREFIX+userInfoTo.getUserId();
        }
        BoundHashOperations<String, Object, Object> stringObjectObjectBoundHashOperations = redisTemplate.boundHashOps(redisCartKey);
        return stringObjectObjectBoundHashOperations;
    }
}
