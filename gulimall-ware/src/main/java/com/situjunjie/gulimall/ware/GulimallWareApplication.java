package com.situjunjie.gulimall.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableRabbit
@SpringBootApplication
@MapperScan("com.situjunjie.gulimall.ware.dao")
@EnableDiscoveryClient
@EnableTransactionManagement
@EnableFeignClients(basePackages = {"com.situjunjie.gulimall.ware.feign"})
public class GulimallWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallWareApplication.class, args);
    }

}
