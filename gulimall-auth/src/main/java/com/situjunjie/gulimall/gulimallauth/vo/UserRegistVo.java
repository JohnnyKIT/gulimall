package com.situjunjie.gulimall.gulimallauth.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * 注册表单提交Vo
 */
@Data
public class UserRegistVo {

    @NotEmpty(message = "用户名不能为空")
    @Length(min = 3,max = 10,message = "用户名长度必须为3-10")
    String username;


    @Length(min = 6,max = 20,message = "密码长度必须为6-20")
    @NotEmpty(message = "密码不能为空")
    String password;


    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$",message = "必须填写合法手机号")
    @NotEmpty(message = "手机号不能为空")
    String phone;

    @NotEmpty(message = "短信验证码不能为空")
    String smsCode;
}
