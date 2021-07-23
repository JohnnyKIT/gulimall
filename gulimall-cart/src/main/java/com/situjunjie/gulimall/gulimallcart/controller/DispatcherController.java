package com.situjunjie.gulimall.gulimallcart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DispatcherController {

    @GetMapping("/cartlist.html")
    public String goCartList(){
        return "cartList";
    }

    @GetMapping("/success.html")
    public String goSuccess(){
        return "success";
    }
}
