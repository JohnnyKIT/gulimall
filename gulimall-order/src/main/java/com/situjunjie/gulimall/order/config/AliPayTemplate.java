package com.situjunjie.gulimall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.situjunjie.gulimall.order.vo.AliPayReqVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 支付宝配置类
 */
@Configuration
@ConfigurationProperties(prefix = "alipay")
public class AliPayTemplate {

    @Autowired
    AlipayClient alipayClient;

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2021000117624955";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCv0PIUFKOZvpIfXmQzulq9hBMcY3cnpLxwHZU9xPpOykGAR6OG5yaMra9fLjUXjCJyzOWpYE6wGk/KdfTNaHtWwlhzSegPc8xBsJdj3b3HKba8OFhOQ9hnH3LyVHMrASGRc9PyLsQ3xVoAxyudsJr+mHlRPIf+IzHIeei13kBe8QWGZjSNTV4T+UY4N0FhmFeDIZko2jvNOVITzJDApgvEv3a9Xk90EdF+8tElzov1vYqAkVnkjeFzrt2HGTeXmr3R1qltULgKN5YQ2zcKeDkTI3W//VTQG5yR9B98QlEWDsmmkaYnVlD9dGcgGrk7nYhzhuzpq/oiD10wp7j4xnNFAgMBAAECggEBAIPYwqGSdLmoMzZ499Xe64txz08bvFCrdnDxjmjbHgcWIBbOInDZSYx396r+IOx+t5q104i4Y2om4S1oVePwdYYzilcy1mc4JLYicvV+ZYJE4ve1nWtb754t9GqMMC3Q1FaasRfOQHG+eudYdXn05dcFiv0D0swfooa//WbdvBI1mLaBbp3FctGHAAkHAR7/ms9YmRBWghu+SfZRUlOVsKsvP+jdC5sL6xDrc0uRFIwynutPlkuCDx76mxHNl28nAvfJSDesm9J7/AfTLmMejR5LhYJTkCXxe6eYzD8x0sdjFjmjJY5rIenoSYzfaroYF12bFVuoHD2ZRT5S8mt892UCgYEA3dUReAsf9ms8WAn90XguaDcrvwBSEHSGBE1cE2cMhGtm8uiIpzdGv+7II30UCaM3ZWjqVv6YpvOFLOtc/bfOudQxby+9+u61jmlgiDiWZPH9JxL6ZZHWOJl7irgwZEeEHIe0R91kAOFFpkPrbJm6XW0w8z0ckZKirIlIJAzfyRMCgYEAyuVx5nBU2pEYjnuyEYWeXkA/q4i8lB3CkpDBYUOo72mOYRvHkJQN+dJuYc1loUr1uSzu4/Zb2arsgEiZLfp+yHxdhwj27w4Eml44fiWMFunCra0FgVgROHs7YgwXtP4kGEywu4j0XmMrACbz9PRBjjP9O2lJCDKi7PF77KrwdUcCgYBETlt2r1ZENnj6UvlJCsyfYn2h7qNWVcr+gCOeBdkg3UFyApAxjR/H8erGorkMzqk8HLYr23F91vZnAoI7zeFl7yTZq8XAAsj89Q24ZFRK+QiOg5V+uQ1RmFEmQdkEx9CySEcG6fDlJHih3cAcTKnKItkzzNmDKbfVkOAZ2az+6QKBgQC/iZLY47p3CvqPp54WhHlYqHZyzIaZnO6NxjGITIMyYAeVDNB8YAhBirHBilEizszhntV2Rp+3AHB7Og3BJfSB90WjsIbMbgNSZxgVngR9rPenVOsZi7XWCB+rE5dduX0Bo4RDxapLYHq7jgpHeD5arCGm8GGNZ5/+5okChuyxCQKBgBlbRUSnDt3CgaBW0bx4FGpcXolgAfS1xwYS3pm/AOFl7V3tVfR3gHqfbq7/epaDCDLhBX4hR5TPKX6iJP0gKEJct71JX7CU+KIw+IEJx4RCv+ieJTjtR3oYKQ2mOptoBuwqruX4EKl6rCfLx3VxXk6RTVK90qrwM8B5YfDf9xL1";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2S2jet9KbMK6jsP9sZE5lzEqFJ0Yv6mJYr7qqXC9wo13/uxu3Ni8PPa4Rt7aE+9kGZx96osI3AAWQaLE57rVYMxRmv7TNGFJf5/EpREzTwEpxGFOj7vCf2uRhpxJu/cYpej97ebgfFhBeB+d/V1q6jwoH/JRW57LCNvu0tDiWPmGs3XRFoD9aAp2X/oRUR4AsLfAsFgQIyPPgWPb6wJftnSJexcdpcIJZULrV0TP2nsfPmtOWUfWBqdMRAhjat4nskyMAgqWN0izeWtgdT/kNYx3ZTVHMHpy8PsyT9fpWmzGEdL7Rhj5Jba9tVkVoYBa1CJmB/BbCw4Da9hl16lP9QIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://2e5m885632.wicp.vip/alipay.trade.page.pay-JAVA-UTF-8/notify_url.jsp";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://member.gulimall.com/memberOrderList.html";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 支付宝网关
    public static String log_path = "C:\\";

    //统一过期订单过期时间
    public static String timeoutExpress = "2m";


    /**
     * 初始化Alipay客户端
     * @return
     */
    @Bean
    public AlipayClient alipayClient(){
        return new DefaultAlipayClient(gatewayUrl, app_id, merchant_private_key, "json", charset, alipay_public_key, sign_type);
    }

    public String getAliPayPage(AliPayReqVo vo) throws AlipayApiException {
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(AliPayTemplate.return_url);
        alipayRequest.setNotifyUrl(AliPayTemplate.notify_url);


        alipayRequest.setBizContent("{\"out_trade_no\":\""+ vo.getOutTradeNo() +"\","
                + "\"total_amount\":\""+ vo.getTotal_amount() +"\","
                + "\"subject\":\""+ vo.getSubject() +"\","
                + "\"body\":\""+ vo.getBody() +"\","
                + "\"timeout_express\":\""+ timeoutExpress +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        //若想给BizContent增加其他可选请求参数，以增加自定义超时时间参数timeout_express来举例说明
        //alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
        //		+ "\"total_amount\":\""+ total_amount +"\","
        //		+ "\"subject\":\""+ subject +"\","
        //		+ "\"body\":\""+ body +"\","
        //		+ "\"timeout_express\":\"10m\","
        //		+ "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        //请求参数可查阅【电脑网站支付的API文档-alipay.trade.page.pay-请求参数】章节

        //请求
        String result = alipayClient.pageExecute(alipayRequest).getBody();


        return  result;
    }
}
