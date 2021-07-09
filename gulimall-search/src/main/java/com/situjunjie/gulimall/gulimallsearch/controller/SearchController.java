package com.situjunjie.gulimall.gulimallsearch.controller;

import com.situjunjie.gulimall.gulimallsearch.service.MallSearchService;
import com.situjunjie.gulimall.gulimallsearch.vo.SearchParam;
import com.situjunjie.gulimall.gulimallsearch.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SearchController {

    @Autowired
    MallSearchService searchService;

    @GetMapping("/list.html")
    public String toSearch(SearchParam searchParam, Model model){

        SearchResult result = searchService.search(searchParam);
        model.addAttribute("result",result);
        return "list";
    }
}
