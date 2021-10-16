package com.situjunjie.gulimall.member.web;

import com.alibaba.fastjson.JSON;
import com.situjunjie.common.to.MemberEntity;
import com.situjunjie.common.to.MemberOrderReqTo;
import com.situjunjie.common.utils.R;
import com.situjunjie.gulimall.member.service.feign.OrderFeignService;
import com.situjunjie.gulimall.member.interceptor.LoginUserInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 会员服务相关页面跳转控制器
 */
@Controller
@Slf4j
public class MemberWebController {

    @Autowired
    OrderFeignService orderFeignService;

    /**
     * 跳转到订单页面
     * @param pageNum
     * @return
     */
    @RequestMapping("/memberOrderList.html")
    public String toMemberOrderList(@RequestParam(value = "pageNum",defaultValue = "1")String pageNum, Model model){

        //获取当前登陆用户
        MemberEntity memberEntity = LoginUserInterceptor.threadLocal.get();

        //查询当前登陆用户的所有订单和订单项
        MemberOrderReqTo to = new MemberOrderReqTo();
        to.setMemberId(memberEntity.getId());
        to.setPageNum(pageNum);
        R memberOrder = orderFeignService.getMemberOrder(to);
        //加入作用域并返回跳转
        model.addAttribute("memberOrder",memberOrder);
        //log.info("购物项：{}", JSON.toJSONString(memberOrder));

        return "orderList";
    }
}
