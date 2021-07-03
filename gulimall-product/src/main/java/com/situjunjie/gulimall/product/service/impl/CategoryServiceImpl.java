package com.situjunjie.gulimall.product.service.impl;

import com.situjunjie.gulimall.product.entity.AttrGroupEntity;
import com.situjunjie.gulimall.product.vo.Category2Vo;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.situjunjie.common.utils.PageUtils;
import com.situjunjie.common.utils.Query;

import com.situjunjie.gulimall.product.dao.CategoryDao;
import com.situjunjie.gulimall.product.entity.CategoryEntity;
import com.situjunjie.gulimall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

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

    @Override
    public List<CategoryEntity> getFirstLevelCategory() {
        return this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid",0));
    }

    @Override
    public Map<String, List<Category2Vo>> getCategoryLevel2() {
        //0.先生成Map准备保存数据
        Map<String, List<Category2Vo>> map = new HashMap<>();
        //1.获取到所有level2的entity
        List<CategoryEntity> level2Entities = this.list(new QueryWrapper<CategoryEntity>().eq("cat_level", 2));
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
            List<CategoryEntity> level3entities = list(new QueryWrapper<CategoryEntity>().eq("parent_cid", level2.getId()));
            //level3的entity map 转换为 level3vo
            List<Category2Vo.Category3Vo> collect3 = level3entities.stream().map(level3entity -> {
                Category2Vo.Category3Vo level3vo = new Category2Vo.Category3Vo(level2.getId(), level3entity.getCatId().toString(), level3entity.getName());
                return level3vo;
            }).collect(Collectors.toList());
            level2.setCatalog3List(collect3);
            map.get(level2.getCatalog1Id()).add(level2);
        });
        

        return map;
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