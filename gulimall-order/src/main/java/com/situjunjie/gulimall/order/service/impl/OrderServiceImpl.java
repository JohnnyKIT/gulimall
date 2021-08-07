package com.situjunjie.gulimall.order.service.impl;

import com.situjunjie.common.to.MemberEntity;
import com.situjunjie.gulimall.order.constant.OrderConst;
import com.situjunjie.gulimall.order.inteceptor.LoginUserInterceptor;
import com.situjunjie.gulimall.order.vo.OrderSubmitResponseVo;
import com.situjunjie.gulimall.order.vo.OrderSubmitVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.situjunjie.common.utils.PageUtils;
import com.situjunjie.common.utils.Query;

import com.situjunjie.gulimall.order.dao.OrderDao;
import com.situjunjie.gulimall.order.entity.OrderEntity;
import com.situjunjie.gulimall.order.service.OrderService;
import org.springframework.util.StringUtils;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
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
                //TODO 下单操作
            }else{
                //校验失败
                responseVo.setCode(1);
                return responseVo;
            }
        }
        responseVo.setCode(1);
        return responseVo;
    }

}