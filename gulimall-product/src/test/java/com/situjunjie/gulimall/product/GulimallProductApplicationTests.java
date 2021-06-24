package com.situjunjie.gulimall.product;

import com.situjunjie.gulimall.product.entity.BrandEntity;
import com.situjunjie.gulimall.product.entity.CategoryEntity;
import com.situjunjie.gulimall.product.service.BrandService;
import com.situjunjie.gulimall.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@Slf4j
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

    @Test
    void testGetCategroyPath(){
        Long[] catelogPath = categoryService.getCatelogPath(225L);
        List<Long> longs = Arrays.asList(catelogPath);
        log.info("输出路径：{}",longs);
    }



}
