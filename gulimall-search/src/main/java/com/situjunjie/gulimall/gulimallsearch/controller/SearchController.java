package com.situjunjie.gulimall.gulimallsearch.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SearchController {

    @GetMapping("/list.html")
    public String toSearch(){

        return "list";
    }
}
