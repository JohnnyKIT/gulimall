package com.situjunjie.gulimall.product.web;

import com.situjunjie.gulimall.product.entity.CategoryEntity;
import com.situjunjie.gulimall.product.service.CategoryService;
import com.situjunjie.gulimall.product.vo.Category2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;

    @GetMapping({"/"})
    public String indexPage(Model model){

        List<CategoryEntity> categoryEntities = categoryService.getFirstLevelCategory();
        model.addAttribute("categories",categoryEntities);
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/json/catalog")
    public Map<String,List<Category2Vo>> getCategoryLevel2(){

        return categoryService.getCategoryLevel2();

    }
}
