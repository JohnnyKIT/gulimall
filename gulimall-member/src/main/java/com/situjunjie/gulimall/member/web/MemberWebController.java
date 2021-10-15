package com.situjunjie.gulimall.member.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MemberWebController {

    @RequestMapping("/memberOrderList.html")
    public String toMemberOrderList(){

        return "orderList";
    }
}
