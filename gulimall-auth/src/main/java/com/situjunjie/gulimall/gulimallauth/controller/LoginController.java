package com.situjunjie.gulimall.gulimallauth.controller;

import com.alibaba.fastjson.TypeReference;
import com.situjunjie.common.utils.R;
import com.situjunjie.gulimall.gulimallauth.feign.MemberFeignService;
import com.situjunjie.gulimall.gulimallauth.vo.UserLoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    MemberFeignService memberFeignService;

    @PostMapping("/login")
    public String userLogin(UserLoginVo vo, RedirectAttributes redirectAttributes){

        //调用Member服务进行登录验证
        R r = memberFeignService.memberLogin(vo);
        if(r.getCode()!=0){
            //登录失败
            Map<String,String> errors = new HashMap<>();
            errors.put("msg",r.getData("msg",new TypeReference<String>(){}));
            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.gulimall.com/login.html";
        }

        return "redirect:http://gulimall.com";
    }
}
