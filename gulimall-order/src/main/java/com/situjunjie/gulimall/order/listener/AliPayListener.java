package com.situjunjie.gulimall.order.listener;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.situjunjie.gulimall.order.config.AliPayTemplate;
import com.situjunjie.gulimall.order.service.OrderService;
import com.situjunjie.gulimall.order.vo.PayAsyncVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 用于处理支付宝交易的异步回调
 */
@RestController
@Slf4j
public class AliPayListener {

    @Autowired
    OrderService orderService;

    /**
     * 处理支付成功后的异步回调
     * @return
     */
    @PostMapping("/payed/notify")
    public String payedNotify(PayAsyncVo vo, HttpServletRequest request) throws AlipayApiException {
        log.info("收到支付宝异步回调通知");
        //获取请求参数准备验证签名
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        //验证签名
        boolean signVerified = AlipaySignature.rsaCheckV1(params, AliPayTemplate.alipay_public_key, AliPayTemplate.charset, AliPayTemplate.sign_type); //调用SDK验证签名
        if(signVerified){
            String result = orderService.payNotifyHandle(vo);
            return result;
        }

        return "error";
    }
}
