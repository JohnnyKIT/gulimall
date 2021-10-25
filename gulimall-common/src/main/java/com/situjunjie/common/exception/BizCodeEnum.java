package com.situjunjie.common.exception;

public enum BizCodeEnum {

    //1.通用类 10
    //2.用户类 20
    //3. 库存 30
    UNKNOW_EXPTION(10000,"系统未知异常"),
    VALID_EXPTION(10001,"参数校验异常"),
    SQL_EXPTION(10002,"SQL异常"),
    SMS_CODE_TOOFAST(10003,"获取短信验证码过于频繁"),
    TOO_MANY_REQUEST(10004,"请求量过大"),
    USERNAME_EXISTS_EXCETION(20001,"用户名已存在"),
    USERNAME_PASSWORD_INVALID_EXCEPTION(20002,"用户名或密码错误"),
    NO_STOCK_EXCEPTION(30000,"库存不足");


    private Integer code;
    private String message;
    BizCodeEnum(int code,String message){
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
