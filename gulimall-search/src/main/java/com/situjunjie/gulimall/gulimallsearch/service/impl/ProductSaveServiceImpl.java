package com.situjunjie.gulimall.gulimallsearch.service.impl;

import com.alibaba.fastjson.JSON;
import com.situjunjie.common.constant.ProductConst;
import com.situjunjie.common.to.es.SkuEsModel;
import com.situjunjie.gulimall.gulimallsearch.config.GulimallElasticSearchConfig;
import com.situjunjie.gulimall.gulimallsearch.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class ProductSaveServiceImpl implements ProductSaveService {

    @Autowired
    RestHighLevelClient esClient;

    @Override
    public void saveSkuEsModel(SkuEsModel skuEsModel) throws IOException {
        log.debug("saveSkuEsModel 开始");
        BulkRequest bulkRequest = new BulkRequest();
        IndexRequest indexRequest = new IndexRequest(ProductConst.ELASTICSEARCH_INDEX_NAME);
        indexRequest.id(skuEsModel.getSkuId().toString());
        String s = JSON.toJSONString(skuEsModel);
        indexRequest.source(s, XContentType.JSON);
        bulkRequest.add(indexRequest);
        BulkResponse responses = esClient.bulk(bulkRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);
        log.debug("Es 插入 product 的 response ={}",responses.toString());
    }
}
