package com.situjunjie.gulimall.coupon;


import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@SpringBootTest
public class GulimallCouponApplicationTests {

    @Test
    public void testLocalDate(){
        String begin = LocalDate.now().atTime(LocalTime.MIN).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String end = LocalDate.now().plusDays(3).atTime(LocalTime.MAX).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("begin====>"+begin);
        System.out.println("end====>"+end);
    }

}
