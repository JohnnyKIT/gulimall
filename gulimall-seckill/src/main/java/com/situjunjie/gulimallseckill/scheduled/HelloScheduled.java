package com.situjunjie.gulimallseckill.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

/**
 * 测试定时任务
 */

@Component
@Slf4j
public class HelloScheduled {

//    @Scheduled(cron = "* * * * * ?")
//    @Async
//    public void Hello(){
//        log.info("Hello");
//    }
}
