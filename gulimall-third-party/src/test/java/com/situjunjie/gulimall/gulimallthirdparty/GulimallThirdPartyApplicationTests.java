package com.situjunjie.gulimall.gulimallthirdparty;

import com.situjunjie.gulimall.gulimallthirdparty.component.SmsComponent;
import com.situjunjie.gulimall.gulimallthirdparty.util.HttpUtils;
import org.apache.http.HttpResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GulimallThirdPartyApplicationTests {

    @Autowired
    SmsComponent smsComponent;
    @Test
    public void contextLoads() {
    }

    /**
     * 测试发送短信验证码
     */
    @Test
    public void SendSms(){
        smsComponent.sendSmsCode("16657160605","1234");
    }

}
