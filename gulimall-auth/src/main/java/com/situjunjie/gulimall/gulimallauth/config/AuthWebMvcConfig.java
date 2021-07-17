package com.situjunjie.gulimall.gulimallauth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvc的相关自定义配置
 */
@Configuration
public class AuthWebMvcConfig implements WebMvcConfigurer {

    /**
     * 直接配置跳转，避免写空方法
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/reg.html").setViewName("reg");
    }
}
