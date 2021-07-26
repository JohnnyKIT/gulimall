package com.situjunjie.gulimall.order.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class DispatcherController {

    @GetMapping("/{page}")
    public String toPage(@PathVariable("page")String page){
        return page;
    }
}
