package com.situjunjie.gulimall.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * 配置线程池
 */
@Configuration
public class ThreadPoolConfig {

    @Bean
    public ExecutorService executorService(ThreadPoolConfigProperties threadPoolConfigProperties){
        System.out.println("threadPoolConfigProperties="+threadPoolConfigProperties);
       return new ThreadPoolExecutor(threadPoolConfigProperties.getCorePoolSize(),threadPoolConfigProperties.getMaximumPoolSize(),threadPoolConfigProperties.getKeepAliveTime(), TimeUnit.SECONDS,new LinkedBlockingQueue<>(), Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());
    }
}
