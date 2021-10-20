package com.situjunjie.gulimallseckill.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 开启异步任务、定时任务的配置类
 */
@Configuration
@EnableAsync
@EnableScheduling
public class ScheduledConfig {
}
