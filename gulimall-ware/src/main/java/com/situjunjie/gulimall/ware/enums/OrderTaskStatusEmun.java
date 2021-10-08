package com.situjunjie.gulimall.ware.enums;

public enum OrderTaskStatusEmun {

    Locked(1,"已锁定"),Unlocked(2,"已解锁"),Counted(3,"已扣减");


    OrderTaskStatusEmun(int i, String message) {
        this.code=i;
        this.message =message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    Integer code;
    String message;



}
