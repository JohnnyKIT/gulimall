package com.situjunjie.gulimall.product.exception;

import com.situjunjie.common.exception.BizCodeEnum;
import com.situjunjie.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.HashMap;

@RestControllerAdvice(basePackages = {"com.situjunjie.gulimall.product.controller"})
@Slf4j
public class GuliExceptionAdvise{

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R ValidExceptionHandle(MethodArgumentNotValidException e){
        HashMap<String,String> erros = new HashMap<>();
        BindingResult result = e.getBindingResult();
        result.getFieldErrors().forEach(item->{
            erros.put(item.getField(),item.getDefaultMessage());
        });
        return R.error(BizCodeEnum.VALID_EXPTION.getCode(),BizCodeEnum.VALID_EXPTION.getMessage()).put("data",erros);
    }

    @ExceptionHandler(Exception.class)
    public R UnknowExptionHandle(Exception e){
        return R.error(BizCodeEnum.UNKNOW_EXPTION.getCode(), BizCodeEnum.UNKNOW_EXPTION.getMessage());
    }

    @ExceptionHandler(java.sql.SQLException.class)
    public R SQLExceptionHandle(SQLException e){
        return R.error(BizCodeEnum.SQL_EXPTION.getCode(),BizCodeEnum.SQL_EXPTION.getMessage());
    }

}
