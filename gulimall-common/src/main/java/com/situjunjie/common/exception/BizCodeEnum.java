package com.situjunjie.common.exception;

public enum BizCodeEnum {

    UNKNOW_EXPTION(10000,"系统未知异常"),
    VALID_EXPTION(10001,"参数校验异常");

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
