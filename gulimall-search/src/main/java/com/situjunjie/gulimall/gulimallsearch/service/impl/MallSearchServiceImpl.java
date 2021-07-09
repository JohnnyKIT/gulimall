package com.situjunjie.gulimall.gulimallsearch.service.impl;

import com.alibaba.fastjson.JSON;
import com.situjunjie.common.constant.ProductConst;
import com.situjunjie.common.to.es.SkuEsModel;
import com.situjunjie.gulimall.gulimallsearch.config.GulimallElasticSearchConfig;
import com.situjunjie.gulimall.gulimallsearch.constant.EsConst;
import com.situjunjie.gulimall.gulimallsearch.service.MallSearchService;
import com.situjunjie.gulimall.gulimallsearch.vo.SearchParam;
import com.situjunjie.gulimall.gulimallsearch.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.TotalHits;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.ParsedAggregation;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        searchResult = buildSearchProductResult(searchResponse,searchParam);

        return searchResult;
    }

    /**
     * 根据ElasticSearch查询返回的Response封装返回的Result
     * @param searchResponse
     * @return
     */
    private SearchResult buildSearchProductResult(SearchResponse searchResponse,SearchParam searchParam) {
        //1.初始化需要的对象
        SearchResult searchResult = new SearchResult();
        List<SkuEsModel> products = new ArrayList<>();
        List<SearchResult.BrandVo> brands = new ArrayList<>();
        List<SearchResult.CatalogVo> catalogs = new ArrayList<>();
        List<SearchResult.AttrVo> attrs = new ArrayList<>();
        List<Integer> pageNavs = new ArrayList<>();
        searchResult.setProducts(products);
        searchResult.setBrands(brands);
        searchResult.setCatalogs(catalogs);
        searchResult.setAttrs(attrs);
        //2.封装所有检索到的商品信息
        SearchHits hits = searchResponse.getHits();
        for(SearchHit hit:hits.getHits()){
            String sourceAsString = hit.getSourceAsString();
            SkuEsModel skuEsModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
            //2.1把高亮结果封装到skuTitle
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField skuTitle = highlightFields.get("skuTitle");
            skuEsModel.setSkuTitle(skuTitle.getFragments()[0].string());
            products.add(skuEsModel);
        }
        //3.封装分页信息
        //3.1当前页
        searchResult.setPageNum(searchParam.getPageNum());
        //3.2总记录数
        searchResult.setTotal(hits.getTotalHits().value);
        //总页码
        Integer total = Math.toIntExact(searchResult.getTotal());
        Integer totalPages = Math.toIntExact(total % EsConst.SEARCH_PAGE_SIZE == 0 ? (searchResult.getTotal() / EsConst.SEARCH_PAGE_SIZE) : (searchResult.getTotal() / EsConst.SEARCH_PAGE_SIZE + 1));
        searchResult.setTotalPages(totalPages);

        //4.封装聚合信息
        //4.1 封装品牌聚合
        Aggregations aggregations = searchResponse.getAggregations();
        ParsedLongTerms brandAggs = aggregations.get("BrandAggs");
        List<? extends Terms.Bucket> brandBuckets = brandAggs.getBuckets();
        for(Terms.Bucket bucket:brandBuckets){
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            brandVo.setBrandId(Long.parseLong(bucket.getKeyAsString()));
            ParsedStringTerms brandImgAgg = bucket.getAggregations().get("BrandImgAgg");
            brandVo.setBrandImg(brandImgAgg.getBuckets().get(0).getKeyAsString());
            ParsedStringTerms brandNameAgg = bucket.getAggregations().get("BrandNameAgg");
            brandVo.setBrandName(brandNameAgg.getBuckets().get(0).getKeyAsString());
            brands.add(brandVo);
        }
        //4.2 封装分类聚合
        ParsedLongTerms categoryAggs = aggregations.get("categoryAggs");
        List<? extends Terms.Bucket> categoryAggsBuckets = categoryAggs.getBuckets();
        for(Terms.Bucket bucket:categoryAggsBuckets){
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            catalogVo.setCatalogId(Long.parseLong(bucket.getKeyAsString()));
            ParsedStringTerms catelogNameAgg = bucket.getAggregations().get("catelogNameAgg");
            catalogVo.setCatalogName(catelogNameAgg.getBuckets().get(0).getKeyAsString());
            catalogs.add(catalogVo);
        }
        //4.3 封装属性聚合
        ParsedNested attrAggs = aggregations.get("attrAggs");
        ParsedLongTerms attrIdAggs = attrAggs.getAggregations().get("attrIdAggs");
        List<? extends Terms.Bucket> attrBuckets = attrIdAggs.getBuckets();
        for(Terms.Bucket attrBucket:attrBuckets){
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            attrVo.setAttrId(Long.parseLong(attrBucket.getKeyAsString()));
            ParsedStringTerms attrNameAggs = attrBucket.getAggregations().get("attrNameAggs");
            attrVo.setAttrName(attrNameAggs.getBuckets().get(0).getKeyAsString());
            ParsedStringTerms attrValueAggs = attrBucket.getAggregations().get("attrValueAggs");
            List<String> collect = attrValueAggs.getBuckets().stream().map(bucket -> {
                return bucket.getKeyAsString();
            }).collect(Collectors.toList());
            attrVo.setAttrValue(collect);
            attrs.add(attrVo);
        }

        return searchResult;
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
        highlightBuilder.preTags("<b color='red'>");
        highlightBuilder.postTags("</b>");
        highlightBuilder.field("skuTitle");
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
        String dsl = searchRequest.source().toString();
        log.info("构建的检索DSL语句={}",dsl);
        return searchRequest;
    }
}
