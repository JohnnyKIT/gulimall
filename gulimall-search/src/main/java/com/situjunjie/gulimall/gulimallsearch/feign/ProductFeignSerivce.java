package com.situjunjie.gulimall.gulimallsearch.feign;

import com.situjunjie.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("gulimall-product")
public interface ProductFeignSerivce {

    @RequestMapping("product/attr/info/{attrId}")
     R getAttrInfoById(@PathVariable("attrId") Long attrId);

    @RequestMapping("product/brand/infos")
     R getBrandBatchByIds(List<Long> brandIds);
}