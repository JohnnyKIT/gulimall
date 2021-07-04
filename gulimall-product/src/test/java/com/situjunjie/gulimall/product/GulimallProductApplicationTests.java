package com.situjunjie.gulimall.product;

import com.alibaba.fastjson.JSON;
import com.situjunjie.gulimall.product.entity.BrandEntity;
import com.situjunjie.gulimall.product.entity.CategoryEntity;
import com.situjunjie.gulimall.product.service.BrandService;
import com.situjunjie.gulimall.product.service.CategoryService;
import com.situjunjie.gulimall.product.vo.Category2Vo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class GulimallProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    RedissonClient redissonClient;



    @Test
    public void contextLoads() {
        List<BrandEntity> list = brandService.list(null);
        list.forEach(System.out::println);

    }

    @Test
    public void testTree(){
        List<CategoryEntity> categoryEntities = categoryService.listWithTree();
        System.out.println(categoryEntities);
    }

    @Test
    public void testGetCategroyPath(){
        Long[] catelogPath = categoryService.getCatelogPath(225L);
        List<Long> longs = Arrays.asList(catelogPath);
        log.info("输出路径：{}",longs);
    }
    
    @Test
    public void getAllCategoryVo() throws InterruptedException {
        Map<String, List<Category2Vo>> categoryLevel2 = categoryService.getCategoryLevel2();
        String s = JSON.toJSONString(categoryLevel2);
        log.info(s);

    }

    /**
     *
     * 测试获取Redisson实例
     */
    @Test
    public void getRedissonClient(){
        System.out.println(redissonClient);
    }



}
