package com.situjunjie.gulimall.order.web;


import com.alibaba.fastjson.TypeReference;
import com.situjunjie.common.to.MemberEntity;
import com.situjunjie.common.to.SkuHasStock;
import com.situjunjie.common.utils.R;
import com.situjunjie.gulimall.order.constant.OrderConst;
import com.situjunjie.gulimall.order.entity.OrderEntity;
import com.situjunjie.gulimall.order.feign.CartFeignService;
import com.situjunjie.gulimall.order.feign.MemberFeignService;
import com.situjunjie.gulimall.order.feign.WareFeignService;
import com.situjunjie.gulimall.order.inteceptor.LoginUserInterceptor;
import com.situjunjie.gulimall.order.service.OrderService;
import com.situjunjie.gulimall.order.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class OrderWebController {

    @Autowired
    OrderService orderService;

    @Autowired
    CartFeignService cartFeignService;

    @Autowired
    MemberFeignService memberFeignService;

    @Autowired
    WareFeignService wareFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ExecutorService executor;

    //测试用例使用
    @Autowired
    RabbitTemplate rabbitTemplate;

    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        //0.准备对象
        OrderConfirmVo vo = new OrderConfirmVo();
        MemberEntity memberEntity = LoginUserInterceptor.threadLocal.get();
        //1.获取当前购物车所有选中的购物项
        //开启异步  需要注意！！！ 加入了Feign的拦截器 ,需要同步不同线程的requestAttribute
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        CompletableFuture<Void> cartItemsFuture = CompletableFuture.runAsync(()->{
            RequestContextHolder.setRequestAttributes(requestAttributes);
            R cartItemsR = cartFeignService.getCartItemsChecked(memberEntity.getId());
            if(cartItemsR.getCode()==0){
                //请求成功获取数据并装配
                List<OrderItemVo> cartItems = cartItemsR.getData("cartItems", new TypeReference<List<OrderItemVo>>() {});
                vo.setItems(cartItems);
            }
        },executor).thenRunAsync(()->{
            //查询库存信息
            List<Long> skuIds = vo.getItems().stream().map(item -> {
                return item.getSkuId();
            }).collect(Collectors.toList());
            R r = wareFeignService.skuHasStock(skuIds);
            List<SkuHasStock> skustock = r.getData(new TypeReference<List<SkuHasStock>>() {
            });
            if(skustock!=null){
                Map<Long, Boolean> collect = skustock.stream().collect(Collectors.toMap(SkuHasStock::getSkuId, SkuHasStock::getHasStock));
                vo.setSkuStock(collect);
            }
        },executor);
        CompletableFuture<Void> addressFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            R addressR = memberFeignService.getMemberReceiveAddress(memberEntity.getId());
            if (addressR.getCode() == 0) {
                //请求成功获取数据并装配
                List<MemberAddressVo> address = addressR.getData("address", new TypeReference<List<MemberAddressVo>>() {
                });
                vo.setMemberAddressVos(address);
            }
        }, executor);
        CompletableFuture.allOf(cartItemsFuture,addressFuture).get();
        vo.setIntegration(memberEntity.getIntegration());

        //防重令牌生成
        String orderToken = UUID.randomUUID().toString().replace("-", "");
        vo.setOrderToken(orderToken);
        redisTemplate.opsForValue().set(OrderConst.USER_ORDER_TOKEN_PREFIX+memberEntity.getId(),orderToken);

        model.addAttribute("orderConfirmVo",vo);

        return "confirm";
    }

    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo vo,Model model){
        log.info("获取到的页面参数OrderSubmitVo={}",vo);
        OrderSubmitResponseVo responseVo = orderService.submitOrder(vo);
        if(responseVo.getCode()==0){
            //成功
            model.addAttribute("responseVo",responseVo);
            return "pay";
        }else{
            //失败
            return "redirect:http://order.gulimall.com/toTrade";
        }

    }

    @ResponseBody
    @GetMapping("/tset/createOrder")
    public String testCreateOrder(){
        OrderEntity order = new OrderEntity();
        order.setOrderSn(UUID.randomUUID().toString());
        order.setModifyTime(new Date());
        System.out.println(order.getModifyTime()+" 创建了订单:" +order.getOrderSn());
        rabbitTemplate.convertAndSend("order-event-exchange","order.create.order",order);
        return "ok";
    }
}
