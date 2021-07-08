package com.situjunjie.gulimall.gulimallsearch.service.impl;

import com.situjunjie.common.constant.ProductConst;
import com.situjunjie.gulimall.gulimallsearch.config.GulimallElasticSearchConfig;
import com.situjunjie.gulimall.gulimallsearch.constant.EsConst;
import com.situjunjie.gulimall.gulimallsearch.service.MallSearchService;
import com.situjunjie.gulimall.gulimallsearch.vo.SearchParam;
import com.situjunjie.gulimall.gulimallsearch.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class MallSearchServiceImpl implements MallSearchService {

    @Autowired
    RestHighLevelClient client;

    @Override
    public SearchResult search(SearchParam searchParam) {
        //1.构建SearchResut要返回的对象
        SearchResult searchResult = null;
        //2.构建查询DSL
        SearchRequest searchRequest = buildSearchProductRequest(searchParam);
        //3.执行查询

        SearchResponse searchResponse = null;
        try {
            searchResponse = client.search(searchRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("检索商品出现异常");
        }
        searchRequest = buildSearchProductResult(searchResponse);

        return searchResult;
    }

    /**
     * 根据ElasticSearch查询返回的Response封装返回的Result
     * @param searchResponse
     * @return
     */
    private SearchRequest buildSearchProductResult(SearchResponse searchResponse) {



        return null;
    }

    /**
     * 根据查询参数构建查询Request
     * @param searchParam 查询参数
     * @return
     */
    private SearchRequest buildSearchProductRequest(SearchParam searchParam) {

        //1.开始构建查询
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        searchSourceBuilder.query(boolQueryBuilder);
        //2.构建关键字模糊匹配
        if(!StringUtils.isEmpty(searchParam.getKeyword())){
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle",searchParam.getKeyword()));
        }
        //3.构建三级分类筛选
        if(!StringUtils.isEmpty(searchParam.getCatalog3Id())){
            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId",searchParam.getCatalog3Id()));
        }
        //4.构建品牌筛选
        if(!StringUtils.isEmpty(searchParam.getBrandId())){
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId",searchParam.getBrandId()));
        }
        // 5.构建属性筛选
        if(searchParam.getAttrs()!=null&&searchParam.getAttrs().size()>0){
            List<String> attrs = searchParam.getAttrs();
            //遍历每个属性,每个属性都是nested嵌入式筛选
            attrs.forEach(attr->{
                //每个属性的结构是：attrs=1_5寸:8寸&2_16G:8G
                String[] s = attr.split("_");
                String[] attrValues = s[1].split(":");
                BoolQueryBuilder query = QueryBuilders.boolQuery();
                query.filter(QueryBuilders.termQuery("attrs.attrId",s[0]));
                query.filter(QueryBuilders.termsQuery("attrs.attrValue",attrValues));
                boolQueryBuilder.filter(QueryBuilders.nestedQuery("attrs", query, ScoreMode.None));
            });
        }

        //6.构建库存筛选
        if(searchParam.getHasStock()!=null){
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock",searchParam.getHasStock()==1));
        }
        //7.构建价格区间筛选
        if(!StringUtils.isEmpty(searchParam.getSkuPrice())){
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("skuPrice");
            String[] range = searchParam.getSkuPrice().split("_");
            if(range.length==2&&StringUtils.isEmpty(range[0])&&StringUtils.isEmpty(range[1])){
                rangeQueryBuilder.gte(range[0]).lte(range[1]);
            }
            if(searchParam.getSkuPrice().startsWith("_")){
                rangeQueryBuilder.lte(range[range.length-1]);
            }
            if(searchParam.getSkuPrice().endsWith("_")){
                rangeQueryBuilder.gte(range[0]);
            }
            boolQueryBuilder.filter(rangeQueryBuilder);
        }
        //8.构建分页
        Integer pageNum = searchParam.getPageNum();
        searchSourceBuilder.from((pageNum-1)*EsConst.SEARCH_PAGE_SIZE);
        searchSourceBuilder.size(EsConst.SEARCH_PAGE_SIZE);
        //9.构建高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<b>");
        highlightBuilder.postTags("</b>");
        searchSourceBuilder.highlighter(highlightBuilder);
        //10.构建聚合
        //10.1.品牌聚合
        TermsAggregationBuilder brandAgg = AggregationBuilders.terms("BrandAggs").field("brandId").size(50);
        brandAgg.subAggregation(AggregationBuilders.terms("BrandNameAgg").field("brandName").size(1));
        brandAgg.subAggregation(AggregationBuilders.terms("BrandImgAgg").field("brandImg").size(1));
        searchSourceBuilder.aggregation(brandAgg);
        //10.2 分类聚合
        TermsAggregationBuilder categoryAgg = AggregationBuilders.terms("categoryAggs").field("catalogId").size(50);
        categoryAgg.subAggregation(AggregationBuilders.terms("catelogNameAgg").field("catalogName").size(1));
        searchSourceBuilder.aggregation(categoryAgg);
        //10.3 属性聚合
        NestedAggregationBuilder attrAggs = AggregationBuilders.nested("attrAggs", "attrs");
        TermsAggregationBuilder attrIdAggs = AggregationBuilders.terms("attrIdAggs").field("attrs.attrId").size(10);
        attrIdAggs.subAggregation(AggregationBuilders.terms("attrNameAggs").field("attrs.attrName").size(10));
        attrIdAggs.subAggregation(AggregationBuilders.terms("attrValueAggs").field("attrs.attrValue").size(10));
        attrAggs.subAggregation(attrIdAggs);
        searchSourceBuilder.aggregation(attrAggs);

        SearchRequest searchRequest = new SearchRequest(new String[]{EsConst.ELASTICSEARCH_INDEX_NAME},searchSourceBuilder);
        String dsl = searchRequest.toString();
        System.out.println("构建的DSL语句 = "+dsl);
        return null;
    }
}
