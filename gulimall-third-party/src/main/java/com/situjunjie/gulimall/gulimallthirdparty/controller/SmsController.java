package com.situjunjie.gulimall.gulimallthirdparty.controller;

import com.situjunjie.common.utils.R;
import com.situjunjie.gulimall.gulimallthirdparty.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短信发送接口
 */

@RestController
@RequestMapping("/sms")
public class SmsController {

    @Autowired
    SmsComponent smsComponent;

    @RequestMapping("/sendCode")
    public R sendCode(@RequestParam("phoneNum")String phoneNum, @RequestParam("code")String code){
        smsComponent.sendSmsCode(phoneNum,code);
        return R.ok();
    }
}
