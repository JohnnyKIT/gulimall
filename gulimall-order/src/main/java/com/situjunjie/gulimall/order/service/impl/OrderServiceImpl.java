package com.situjunjie.gulimall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.situjunjie.common.to.MemberEntity;
import com.situjunjie.common.to.MemberOrderReqTo;
import com.situjunjie.common.utils.Constant;
import com.situjunjie.common.utils.R;
import com.situjunjie.gulimall.order.constant.OrderConst;
import com.situjunjie.gulimall.order.entity.OrderItemEntity;
import com.situjunjie.gulimall.order.enume.OrderStatusEnum;
import com.situjunjie.gulimall.order.feign.CartFeignService;
import com.situjunjie.gulimall.order.feign.MemberFeignService;
import com.situjunjie.gulimall.order.feign.ProductFeignService;
import com.situjunjie.gulimall.order.feign.WareFeignService;
import com.situjunjie.gulimall.order.inteceptor.LoginUserInterceptor;
import com.situjunjie.gulimall.order.service.OrderItemService;
import com.situjunjie.gulimall.order.vo.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.situjunjie.common.utils.PageUtils;
import com.situjunjie.common.utils.Query;

import com.situjunjie.gulimall.order.dao.OrderDao;
import com.situjunjie.gulimall.order.entity.OrderEntity;
import com.situjunjie.gulimall.order.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    OrderItemService orderItemService;

    @Autowired
    CartFeignService cartFeignService;

    @Autowired
    MemberFeignService memberFeignService;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    WareFeignService wareFeignService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public OrderSubmitResponseVo submitOrder(OrderSubmitVo vo) {
        //准备返回对象
        OrderSubmitResponseVo responseVo = new OrderSubmitResponseVo();
        final MemberEntity memberEntity = LoginUserInterceptor.threadLocal.get();
        //1.校验防重令牌
        if(!StringUtils.isEmpty(vo.getOrderToken())){
            String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
            Long execute = redisTemplate.execute(redisScript, Arrays.asList(OrderConst.USER_ORDER_TOKEN_PREFIX + memberEntity.getId()), vo.getOrderToken());
            if(execute==1l){
                //成功校验令牌
                //构建订单
                OrderEntity orderEntity = buildOrder(vo);
                List<OrderItemEntity> orderItems = null ;
                //构建订单项
                R r = cartFeignService.getCartItemsChecked(memberEntity.getId());
                if(r.getCode()==0){
                    List<OrderItemVo> cartItems = r.getData("cartItems", new TypeReference<List<OrderItemVo>>(){});
                    orderItems = buildOrderItems(cartItems,orderEntity);
                }
                //验价
                if (vo.getPayPrice().subtract(orderEntity.getPayAmount()).abs().doubleValue()<0.01){
                    //验价通过 保存订单和订单项目
                    saveOrder(orderEntity,orderItems);
                    //库存锁定
                    LockOrderStockVo lockOrderStockVo = buildOrderLockVo(orderItems,orderEntity);
                    //生成库存锁定工作单

                    R resp = wareFeignService.lockStock(lockOrderStockVo);
                    if(resp.getCode()==0){
                        //调用锁库存方法成功
                        responseVo.setCode(0);
                        responseVo.setOrderEntity(orderEntity);
                        return responseVo;
                    }else{
                        //锁库存发生错误
                        responseVo.setCode(1);
                        return responseVo;
                    }
                }else{
                    log.error("验价不通过");
                    responseVo.setCode(1);
                    return responseVo;
                }

            }else{
                //校验失败
                responseVo.setCode(1);
                return responseVo;
            }
        }
        responseVo.setCode(1);
        return responseVo;
    }

    /**
     * 过期订单关闭
     * @param order
     */
    @Override
    public void releaseOrder(OrderEntity order) {
        OrderEntity update = new OrderEntity();
        update.setId(order.getId());
        update.setStatus(OrderStatusEnum.CANCLED.getCode());
        this.updateById(update);
    }

    private LockOrderStockVo buildOrderLockVo(List<OrderItemEntity> orderItems, OrderEntity orderEntity) {
        LockOrderStockVo vo = new LockOrderStockVo();
        vo.setOrderSn(orderEntity.getOrderSn());
        List<OrderItemVo> collect = orderItems.stream().map(orderItemEntity -> {
            OrderItemVo orderItemVo = new OrderItemVo();
            orderItemVo.setSkuId(orderItemEntity.getSkuId());
            orderItemVo.setCount(orderItemEntity.getSkuQuantity());
            return orderItemVo;
        }).collect(Collectors.toList());
        vo.setItems(collect);
        return vo;
    }

    /**
     * 保存订单和订单项到数据库
     * @param orderEntity
     * @param orderItems
     */
    private void saveOrder(OrderEntity orderEntity, List<OrderItemEntity> orderItems) {
        //save(orderEntity);
        orderItemService.saveBatch(orderItems);
    }

    /**
     * 构建订单项目
     * @param cartItems
     * @return
     */
    private List<OrderItemEntity> buildOrderItems(List<OrderItemVo> cartItems,OrderEntity order) {
        List<OrderItemEntity> collect = cartItems.stream().map(cartItem -> {
            OrderItemEntity orderItemEntity = new OrderItemEntity();
            orderItemEntity.setOrderId(order.getId());
            orderItemEntity.setOrderSn(order.getOrderSn());
            //1.构建sku数据
            orderItemEntity.setSkuId(cartItem.getSkuId());
            String skuAttrList = StringUtils.arrayToDelimitedString(cartItem.getSkuAttr().toArray(new String[0]), ";");
            orderItemEntity.setSkuAttrsVals(skuAttrList);
            orderItemEntity.setSkuPic(cartItem.getImage());
            orderItemEntity.setSkuPrice(cartItem.getPrice());
            orderItemEntity.setSkuQuantity(cartItem.getCount());
            orderItemEntity.setSkuName(cartItem.getTitle());
            //2.远程获取Spu数据并构建
            R resp = productFeignService.getSpuInfoBySkuId(cartItem.getSkuId());
            SpuInfoVo spuInfo = resp.getData(new TypeReference<SpuInfoVo>(){});
            orderItemEntity.setSpuId(spuInfo.getId());
            orderItemEntity.setCategoryId(spuInfo.getCatalogId());
            orderItemEntity.setSpuName(spuInfo.getSpuName());
            orderItemEntity.setSpuPic(cartItem.getImage());
            //3.设置成长值信息
            orderItemEntity.setGiftGrowth(cartItem.getPrice().intValue());
            orderItemEntity.setGiftIntegration(cartItem.getPrice().intValue());
            //4.状态信息

            return orderItemEntity;
        }).collect(Collectors.toList());
        //根据数据库的信息再次计算总价格
        computePrice(order,collect);
        return collect;
    }

    /**
     * 重新计算订单总额
     * @param order
     * @param orderItems
     */
    private void computePrice(OrderEntity order, List<OrderItemEntity> orderItems) {

        BigDecimal total = new BigDecimal("0.0");
        BigDecimal integration = new BigDecimal("0.0");
        BigDecimal coupon = new BigDecimal("0.0");
        BigDecimal promotion = new BigDecimal("0.0");
        BigDecimal pay;
        for (OrderItemEntity orderItem : orderItems) {
            total = total.add(orderItem.getSkuPrice().multiply(new BigDecimal(orderItem.getSkuQuantity())));
            if(orderItem.getIntegrationAmount()!=null)
                integration = integration.add(orderItem.getIntegrationAmount());
            if(orderItem.getPromotionAmount()!=null)
                promotion = promotion.add(orderItem.getPromotionAmount());
            if(orderItem.getCouponAmount()!=null)
                coupon = coupon.add(orderItem.getCouponAmount());
        }
        pay = total.subtract(integration).subtract(coupon).subtract(promotion).add(order.getFreightAmount());
        order.setTotalAmount(total);
        order.setPayAmount(pay);
        order.setPromotionAmount(promotion);
        order.setIntegration(integration.intValue());


    }

    /**
     * 构建当前购物车订单
     * @return
     */
    private OrderEntity buildOrder(OrderSubmitVo vo) {
        //1.准备对象
        OrderEntity orderEntity = new OrderEntity();
        MemberEntity memberEntity = LoginUserInterceptor.threadLocal.get();
        //2.装配对象
        orderEntity.setMemberId(memberEntity.getId());
        orderEntity.setOrderSn(IdWorker.getTimeId());
        orderEntity.setCreateTime(new Date());
        orderEntity.setMemberUsername(memberEntity.getUsername());
        orderEntity.setTotalAmount(vo.getPayPrice());
        orderEntity.setPayAmount(vo.getPayPrice());
        R r = memberFeignService.calFareByAddrId(vo.getAddrId());
        if(r.getCode()==0){
            BigDecimal fare = r.getData("fare", new TypeReference<BigDecimal>(){});
            MemberAddressVo addressVo = r.getData("selectedAddr",new TypeReference<MemberAddressVo>(){});
            orderEntity.setFreightAmount(fare);
            orderEntity.setReceiverName(addressVo.getName());
            orderEntity.setReceiverPhone(addressVo.getPhone());
            orderEntity.setReceiverPostCode(addressVo.getPostCode());
            orderEntity.setReceiverProvince(addressVo.getProvince());
            orderEntity.setReceiverCity(addressVo.getCity());
            orderEntity.setReceiverRegion(addressVo.getRegion());
            orderEntity.setReceiverDetailAddress(addressVo.getDetailAddress());
            orderEntity.setNote(vo.getNote());
        }
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setAutoConfirmDay(14);
        orderEntity.setGrowth(10);
        orderEntity.setConfirmStatus(0);
        orderEntity.setDeleteStatus(0);
        //保存至数据库
        int insert = this.baseMapper.insert(orderEntity);
        rabbitTemplate.convertAndSend("order-event-exchange","order.create.order", orderEntity);
        System.out.println("成功插入订单数量 = "+insert);
        return orderEntity;
    }

    /**
     * 查询用户订单列表页的数据
     * @param memberOrderReqTo
     * @return
     */
    @Override
    public PageUtils queryMemberOrderPage(MemberOrderReqTo memberOrderReqTo) {
        Map<String,Object> params = new HashMap<>();
        //设置分页、排序参数
        params.put(Constant.PAGE,memberOrderReqTo.getPageNum());
        params.put(Constant.LIMIT,10);
        params.put(Constant.ORDER,"DESC");
        params.put(Constant.ORDER_FIELD,"id");

        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>().eq("member_id",memberOrderReqTo.getMemberId()));
        //获取records遍历查出订单项并装配到order
        List<OrderEntity> records = page.getRecords();
        records.stream().forEach(order -> {
            List<OrderItemEntity> list = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", order.getOrderSn()));
            order.setItemList(list);
        });
        return new PageUtils(page);
    }



}