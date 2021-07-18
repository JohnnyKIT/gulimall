package com.situjunjie.gulimall.gulimallauth.controller;

import com.situjunjie.common.constant.AuthServerConst;
import com.situjunjie.common.exception.BizCodeEnum;
import com.situjunjie.common.utils.R;
import com.situjunjie.gulimall.gulimallauth.feign.ThirdPartyFeignService;
import com.situjunjie.gulimall.gulimallauth.vo.UserRegistVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/auth")
public class RegisterController {

    @Autowired
    ThirdPartyFeignService thirdPartyFeignService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @ResponseBody
    @RequestMapping("/sendSms")
    public R sendSmsCode(@RequestParam("phoneNum")String phoneNum){

        //1.判断取出校验
        String validCode =stringRedisTemplate.opsForValue().get(AuthServerConst.SMS_CODE_KEY_PREFIX + phoneNum);
        if(!StringUtils.isEmpty(validCode)){
            Long generatedTime = Long.valueOf(validCode.split("_")[1]);
            System.out.println(System.currentTimeMillis()-generatedTime);
            if (System.currentTimeMillis()-generatedTime<60*1000){ //频繁获取验证码
                return R.error(BizCodeEnum.SMS_CODE_TOOFAST.getCode(), BizCodeEnum.SMS_CODE_TOOFAST.getMessage());
            }
        }
        //1.生成验证码
        String code = UUID.randomUUID().toString().replace("-", "").substring(0, 4);
        thirdPartyFeignService.sendCode(phoneNum,code);
        //2.存到Redis中
        stringRedisTemplate.opsForValue().set(AuthServerConst.SMS_CODE_KEY_PREFIX+phoneNum,code+"_"+System.currentTimeMillis(),AuthServerConst.SMS_CODE_LIFETIME, TimeUnit.MILLISECONDS);
        return R.ok();
    }

    /**
     * 用户注册接口
     * @param vo
     * @param bindingResult
     * @return
     */
    @PostMapping("/register")
    public String userRegist(@Valid UserRegistVo vo , BindingResult bindingResult, RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(item->{
                errors.put(item.getField(),item.getDefaultMessage());
            });
            //模拟重定向携带数据
            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.gulimall.com/reg.html";
        }
        //TODO 调用用户注册接口
        return "redirect:/login.html";
    }
}
