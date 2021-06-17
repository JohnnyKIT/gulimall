package com.situjunjie.gulimall.product;

import com.situjunjie.gulimall.product.entity.BrandEntity;
import com.situjunjie.gulimall.product.entity.CategoryEntity;
import com.situjunjie.gulimall.product.service.BrandService;
import com.situjunjie.gulimall.product.service.CategoryService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Test
    void contextLoads() {
        List<BrandEntity> list = brandService.list(null);
        list.forEach(item->{
            System.out.println(item);
        });

    }

    @Test
    void testTree(){
        List<CategoryEntity> categoryEntities = categoryService.listWithTree();
        System.out.println(categoryEntities);
    }

}
