package com.situjunjie.gulimallseckill.controller;

import com.situjunjie.common.utils.R;
import com.situjunjie.gulimallseckill.service.SeckillSkuService;
import com.situjunjie.gulimallseckill.vo.SeckillSkuRedisVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SeckillSessionController {

    @Autowired
    SeckillSkuService seckillSkuService;

    @GetMapping("/seckill/currentSeckillSku")
    public R getCurrentSeckillSku(){
        List<SeckillSkuRedisVo> rs = seckillSkuService.getCurrentSeckillSku();
        return R.ok().setData(rs);
    }

}
