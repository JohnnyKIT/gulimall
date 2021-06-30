package com.situjunjie.gulimall.product.feign;

import com.situjunjie.common.to.es.SkuEsModel;
import com.situjunjie.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("gulimall-search")
public interface ElasticSerachFeignService {

    @PostMapping("/elasticsearch/product-up")
    R saveSkuEsModel(@RequestBody SkuEsModel skuEsModel);

}
