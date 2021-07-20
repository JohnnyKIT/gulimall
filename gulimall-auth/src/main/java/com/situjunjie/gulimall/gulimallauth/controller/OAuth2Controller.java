package com.situjunjie.gulimall.gulimallauth.controller;

import com.alibaba.fastjson.JSON;
import com.situjunjie.common.utils.HttpUtils;
import com.situjunjie.common.utils.R;
import com.situjunjie.gulimall.gulimallauth.feign.MemberFeignService;
import com.situjunjie.gulimall.gulimallauth.vo.WeiboAccessTokenVo;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/auth2.0")
public class OAuth2Controller {

    @Autowired
    MemberFeignService memberFeignService;

    /**
     * 微博社交登录成功
     */
    @RequestMapping("/weibo/success")
    public String weiboLoginSuccess(String code)  {


        //1.请求换取Accesscode
        Map<String,String> body = new HashMap<>();
        body.put("code",code);
        body.put("client_id","2819321120");
        body.put("client_secret","4637d822fcbb1e0e90c460496f9067be");
        body.put("grant_type","authorization_code");
        body.put("redirect_uri","http://auth.gulimall.com/auth2.0/weibo/success");
        try {
            HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "post", new HashMap<>(), null, body);
            HttpEntity entity = response.getEntity();
            String json = EntityUtils.toString(entity);

            System.out.println("json="+json);
            WeiboAccessTokenVo weiboAccessTokenVo = JSON.parseObject(json, WeiboAccessTokenVo.class);
            R r = memberFeignService.weiboLogin(weiboAccessTokenVo);
            if (r.getCode()!=0){
                throw new Exception();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:http://auth.gulimall.com/login.html";
        }

        return "redirect:http://gulimall.com";
    }
}
