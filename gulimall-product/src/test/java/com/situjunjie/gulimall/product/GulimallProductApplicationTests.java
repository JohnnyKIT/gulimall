package com.situjunjie.gulimall.product;

import com.situjunjie.gulimall.product.entity.BrandEntity;
import com.situjunjie.gulimall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Test
    void contextLoads() {
        List<BrandEntity> list = brandService.list(null);
        list.forEach(item->{
            System.out.println(item);
        });

    }

}
