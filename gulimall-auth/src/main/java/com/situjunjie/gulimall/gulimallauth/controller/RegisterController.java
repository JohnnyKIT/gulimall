package com.situjunjie.gulimall.gulimallauth.controller;

import com.alibaba.fastjson.TypeReference;
import com.situjunjie.common.constant.AuthServerConst;
import com.situjunjie.common.exception.BizCodeEnum;
import com.situjunjie.common.utils.R;
import com.situjunjie.gulimall.gulimallauth.feign.MemberFeignService;
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

    @Autowired
    MemberFeignService memberFeignService;

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
        //校验表单 返回异常数据
        if(bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(item->{
                errors.put(item.getField(),item.getDefaultMessage());
            });
            //模拟重定向携带数据
            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.gulimall.com/reg.html";
        }
        //取出短信验证码并校验
        String code = stringRedisTemplate.opsForValue().get(AuthServerConst.SMS_CODE_KEY_PREFIX + vo.getPhone()).split("_")[0];
        if(!code.equals(vo.getSmsCode())){
            Map<String, String> errors = new HashMap<>();
            errors.put("smsCode","验证码不正确");
            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.gulimall.com/reg.html";
        }
        //校验成功删除验证码
        stringRedisTemplate.delete(AuthServerConst.SMS_CODE_KEY_PREFIX + vo.getPhone());
        //调用member服务进行注册
        R r = memberFeignService.memberRegist(vo);
        if(r.getCode()!=0){
            //注册失败
            Map<String, String> errors = new HashMap<>();
            errors.put("msg",r.getData("msg",new TypeReference<String>(){}));
            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.gulimall.com/reg.html";
        }


        return "redirect:http://auth.gulimall.com/login.html";
    }
}
