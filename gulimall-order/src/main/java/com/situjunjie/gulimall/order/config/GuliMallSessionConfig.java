package com.situjunjie.gulimall.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
public class GuliMallSessionConfig {

    /**
     * 自定义Cookie序列化
     * @return
     */
    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("GULISESSION");
        serializer.setDomainName("gulimall.com");
        return serializer;
    }

    /**
     * session数据序列化成json
     * @return
     */
    @Bean
    public RedisSerializer springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }
}
