package com.situjunjie.gulimall.gulimallauth.controller;

import com.alibaba.fastjson.TypeReference;
import com.situjunjie.common.constant.AuthServerConst;
import com.situjunjie.common.to.MemberEntity;
import com.situjunjie.common.utils.R;
import com.situjunjie.gulimall.gulimallauth.feign.MemberFeignService;
import com.situjunjie.gulimall.gulimallauth.vo.UserLoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    MemberFeignService memberFeignService;

    @PostMapping("/auth/login")
    public String userLogin(UserLoginVo vo, RedirectAttributes redirectAttributes,HttpSession session){

        //调用Member服务进行登录验证
        R r = memberFeignService.memberLogin(vo);
        if(r.getCode()!=0){
            //登录失败
            Map<String,String> errors = new HashMap<>();
            errors.put("msg",r.getData("msg",new TypeReference<String>(){}));
            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.gulimall.com/login.html";
        }
        MemberEntity entity = r.getData(AuthServerConst.LOGIN_USER_SESSION, new TypeReference<MemberEntity>() {
        });
        session.setAttribute(AuthServerConst.LOGIN_USER_SESSION,entity);
        return "redirect:http://gulimall.com";
    }

    //直接到登录页时判断session是否有登录信息
    @GetMapping("/login.html")
    public String goLoginPage(HttpSession session){

        if (session.getAttribute(AuthServerConst.LOGIN_USER_SESSION)!=null){
            return "redirect:http://gulimall.com";
        }
        return "login";
    }
}
