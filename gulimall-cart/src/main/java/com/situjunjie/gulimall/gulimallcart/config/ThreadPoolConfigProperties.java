package com.situjunjie.gulimall.gulimallcart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "gulimall.threadpool")
@Component
@Data
public class ThreadPoolConfigProperties {

    private Integer corePoolSize;

    private Integer maximumPoolSize;

    private Integer keepAliveTime;

}
