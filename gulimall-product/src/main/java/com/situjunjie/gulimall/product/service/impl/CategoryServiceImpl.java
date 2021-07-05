package com.situjunjie.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.situjunjie.gulimall.product.entity.AttrGroupEntity;
import com.situjunjie.gulimall.product.vo.Category2Vo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.situjunjie.common.utils.PageUtils;
import com.situjunjie.common.utils.Query;

import com.situjunjie.gulimall.product.dao.CategoryDao;
import com.situjunjie.gulimall.product.entity.CategoryEntity;
import com.situjunjie.gulimall.product.service.CategoryService;
import org.springframework.util.StringUtils;

@Slf4j
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //先查询出所有
        List<CategoryEntity> all = baseMapper.selectList(null);

        //构建树形分类
        return all.stream().filter(categoryEntity -> categoryEntity.getParentCid()==0)
                .map(categoryEntity -> {categoryEntity.setChildrens(getChildrens(categoryEntity,all));
                                        return categoryEntity;
                                        })
                                        .sorted((m1,m2)-> {return (m1.getSort()==null?0:m1.getSort())-(m2.getSort()==null?0:m2.getSort());})
                                        .collect(Collectors.toList());




    }

    @Override
    public Long[] getCatelogPath(Long catelogId) {
        ArrayList<Long> list = new ArrayList<>();
        findCatelogPath(catelogId,list);
        Collections.reverse(list);
        Long[] path = list.toArray(new Long[list.size()]);
        return path;
    }

    @Cacheable(value = {"category"},key = "#root.method.name")
    @Override
    public List<CategoryEntity> getFirstLevelCategory() {
        System.out.println("查数据库获取一级分类");
        return this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid",0));
    }

    @Override
    public Map<String, List<Category2Vo>> getCategoryLevel2() throws InterruptedException {
        String categoryJson = redisTemplate.opsForValue().get("categoryJson");
        if(StringUtils.isEmpty(categoryJson)){
            System.out.println("判断Redis中没数据，准备查数据库。。");
            Map<String, List<Category2Vo>> categoryLevel2FromDb = getCategoryLevel2ByRedisLock();

            return categoryLevel2FromDb;
        }
        System.out.println("Redis中有数据，直接取出缓存");
        Map<String, List<Category2Vo>> stringListMap = JSON.parseObject(categoryJson, new TypeReference<Map<String, List<Category2Vo>>>() {
        });
        return stringListMap;
    }

    public Map<String, List<Category2Vo>> getCategoryLevel2ByRedisLock() throws InterruptedException {

        //改造使用Redisson分布式锁
        RLock rLock = redissonClient.getLock("categoryJsonLock");

        rLock.lock(30,TimeUnit.SECONDS);
//        //1.加入Redis锁，并且加入当前表示线程uuid
//        String uuid = UUID.randomUUID().toString();
//        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 30l, TimeUnit.SECONDS);
//        // lock=true即成功上锁 可以去查数据库

            //查数据库操作
            Map<String, List<Category2Vo>> map = getCategoryLevel2FromDb();
            //查完数据库后解锁,解自己的锁

//            String luaScript ="if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
//                    "then\n" +
//                    "    return redis.call(\"del\",KEYS[1])\n" +
//                    "else\n" +
//                    "    return 0\n" +
//                    "end";
//            RedisScript<Long> script = new DefaultRedisScript<Long>(luaScript,Long.class);
//            Long execute = redisTemplate.execute(script, Arrays.asList("lock"), uuid);

        rLock.unlock();

            return map;



    }



    public Map<String, List<Category2Vo>> getCategoryLevel2FromDb() {

            //加入了本地锁，需要再判断一次缓存的数据
            String categoryJson = redisTemplate.opsForValue().get("categoryJson");
            if(!StringUtils.isEmpty(categoryJson)){
                Map<String, List<Category2Vo>> stringListMap = JSON.parseObject(categoryJson, new TypeReference<Map<String, List<Category2Vo>>>() {
                });
                return stringListMap;
            }
        //性能优化  查分类表全表一次
        List<CategoryEntity> allCategories = this.list(null);
        //0.先生成Map准备保存数据
        Map<String, List<Category2Vo>> map = new HashMap<>();
        //1.获取到所有level2的entity
        List<CategoryEntity> level2Entities = getAlllevel2Category(allCategories,2);
        //映射生成了level2的vo
        List<Category2Vo> collect = level2Entities.stream().map(level2 -> {
            Category2Vo level2vo = new Category2Vo(level2.getParentCid().toString(), level2.getCatId().toString(), level2.getName(), null);
            if (!map.containsKey(level2.getParentCid().toString())){
                map.put(level2.getParentCid().toString(),new ArrayList<>());
            }
            return level2vo;
        }).collect(Collectors.toList());
        //映射生成level3vo
        collect.forEach(level2->{
            //查出level3的entity集合
            //List<CategoryEntity> level3entities = list(new QueryWrapper<CategoryEntity>().eq("parent_cid", level2.getId()));
            List<CategoryEntity> level3entities = allCategories.stream().filter(item -> {
                return item.getParentCid() == Long.parseLong(level2.getId());
            }).collect(Collectors.toList());
            //level3的entity map 转换为 level3vo
            List<Category2Vo.Category3Vo> collect3 = level3entities.stream().map(level3entity -> {
                Category2Vo.Category3Vo level3vo = new Category2Vo.Category3Vo(level2.getId(), level3entity.getCatId().toString(), level3entity.getName());
                return level3vo;
            }).collect(Collectors.toList());
            level2.setCatalog3List(collect3);
            map.get(level2.getCatalog1Id()).add(level2);
        });
        System.out.println("查询了数据库...");
            String s = JSON.toJSONString(map);
            redisTemplate.opsForValue().set("categoryJson",s);
        return map;

    }

    /*
    返回指定分类的实体
     */
    private List<CategoryEntity> getAlllevel2Category(List<CategoryEntity> allCategories,Integer categoryLevel) {
        List<CategoryEntity> collect = allCategories.stream().filter(item -> {
            return item.getCatLevel() == categoryLevel;
        }).collect(Collectors.toList());
        return collect;
    }

    private void findCatelogPath(Long catelogId, ArrayList<Long> list) {
        list.add(catelogId);
        CategoryEntity category = this.getById(catelogId);
        if(category.getParentCid()!=0){
            findCatelogPath(category.getParentCid(),list);
        }

    }

    //递归获取自分类的方法
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {

        return all.stream().filter(menu->{return root.getCatId().equals(menu.getParentCid());})
                .map(categoryEntity -> {categoryEntity.setChildrens(getChildrens(categoryEntity,all));return categoryEntity;})
                .sorted((m1,m2)->{return (m1.getSort()==null?0:m1.getSort())-(m2.getSort()==null?0:m2.getSort());})
                .collect(Collectors.toList());


    }

}