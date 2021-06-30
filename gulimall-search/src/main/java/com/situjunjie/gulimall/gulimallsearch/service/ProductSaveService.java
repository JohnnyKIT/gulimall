package com.situjunjie.gulimall.gulimallsearch.service;


import com.situjunjie.common.to.es.SkuEsModel;

import java.io.IOException;

public interface ProductSaveService {
    void saveSkuEsModel(SkuEsModel skuEsModel) throws IOException;
}
