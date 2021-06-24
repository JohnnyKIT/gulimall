package com.situjunjie.gulimall.product.service.impl;

import com.situjunjie.gulimall.product.entity.AttrGroupEntity;
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