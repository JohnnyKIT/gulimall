package com.situjunjie.gulimall.gulimallauth.vo;

import lombok.Data;

/**
 * 接收前端表单登录的Vo
 */
@Data
public class UserLoginVo {

    private String loginacct;
    private String password;
}
