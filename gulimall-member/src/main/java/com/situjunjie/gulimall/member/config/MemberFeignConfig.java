package com.situjunjie.gulimall.member.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class MemberFeignConfig {

    /**
     * 配置Feign拦截器，要加入Cookie去调用
     * @return
     */
    @Bean("requestInterceptor")
    RequestInterceptor requestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if(requestAttributes!=null){
                    String cookie = requestAttributes.getRequest().getHeader("Cookie");
                    System.out.println("加入cookie="+cookie);
                    requestTemplate.header("Cookie",cookie);
                }
            }
        };
    }
}
