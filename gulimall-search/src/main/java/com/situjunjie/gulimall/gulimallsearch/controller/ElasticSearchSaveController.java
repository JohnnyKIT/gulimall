package com.situjunjie.gulimall.gulimallsearch.controller;

import com.situjunjie.common.to.es.SkuEsModel;
import com.situjunjie.common.utils.R;
import com.situjunjie.gulimall.gulimallsearch.service.ProductSaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequestMapping("/elasticsearch")
@RestController
public class ElasticSearchSaveController {

    @Autowired
    ProductSaveService productSaveService;

    @PostMapping("/product-up")
    public R saveSkuEsModel(@RequestBody SkuEsModel skuEsModel) {

        try {
            productSaveService.saveSkuEsModel(skuEsModel);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.ok();
    }



}
