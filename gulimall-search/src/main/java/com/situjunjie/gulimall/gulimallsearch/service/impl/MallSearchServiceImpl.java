package com.situjunjie.gulimall.gulimallsearch.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.situjunjie.common.to.es.SkuEsModel;
import com.situjunjie.common.utils.R;
import com.situjunjie.gulimall.gulimallsearch.config.GulimallElasticSearchConfig;
import com.situjunjie.gulimall.gulimallsearch.constant.EsConst;
import com.situjunjie.gulimall.gulimallsearch.feign.ProductFeignSerivce;
import com.situjunjie.gulimall.gulimallsearch.service.MallSearchService;
import com.situjunjie.gulimall.gulimallsearch.vo.AttrInfoResp;
import com.situjunjie.gulimall.gulimallsearch.vo.SearchParam;
import com.situjunjie.gulimall.gulimallsearch.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MallSearchServiceImpl implements MallSearchService {

    @Autowired
    RestHighLevelClient client;

    @Autowired
    ProductFeignSerivce productFeignSerivce;

    @Override
    public SearchResult search(SearchParam searchParam) {
        //1.??????SearchResut??????????????????
        SearchResult searchResult = null;
        //2.????????????DSL
        SearchRequest searchRequest = buildSearchProductRequest(searchParam);
        //3.????????????

        SearchResponse searchResponse = null;
        try {
            searchResponse = client.search(searchRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("????????????????????????");
        }
        searchResult = buildSearchProductResult(searchResponse,searchParam);

        return searchResult;
    }

    /**
     * ??????ElasticSearch???????????????Response???????????????Result
     * @param searchResponse
     * @return
     */
    private SearchResult buildSearchProductResult(SearchResponse searchResponse,SearchParam searchParam) {
        //1.????????????????????????
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
        searchResult.setPageNavs(pageNavs);

        //2.????????????????????????????????????
        SearchHits hits = searchResponse.getHits();
        for(SearchHit hit:hits.getHits()){
            String sourceAsString = hit.getSourceAsString();
            SkuEsModel skuEsModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
            //2.1????????????????????????skuTitle

            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if(!highlightFields.isEmpty()){
            HighlightField skuTitle = highlightFields.get("skuTitle");
            skuEsModel.setSkuTitle(skuTitle.getFragments()[0].string());
            }
            products.add(skuEsModel);
        }
        //3.??????????????????
        //3.1?????????
        searchResult.setPageNum(searchParam.getPageNum());
        //3.2????????????
        searchResult.setTotal(hits.getTotalHits().value);
        //?????????
        Integer total = Math.toIntExact(searchResult.getTotal());
        Integer totalPages = Math.toIntExact(total % EsConst.SEARCH_PAGE_SIZE == 0 ? (searchResult.getTotal() / EsConst.SEARCH_PAGE_SIZE) : (searchResult.getTotal() / EsConst.SEARCH_PAGE_SIZE + 1));
        searchResult.setTotalPages(totalPages);

        for (int i = 1; i <= totalPages ; i++) {
            pageNavs.add(i);
        }

        //4.??????????????????
        //4.1 ??????????????????
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
        //4.2 ??????????????????
        ParsedLongTerms categoryAggs = aggregations.get("categoryAggs");
        List<? extends Terms.Bucket> categoryAggsBuckets = categoryAggs.getBuckets();
        for(Terms.Bucket bucket:categoryAggsBuckets){
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            catalogVo.setCatalogId(Long.parseLong(bucket.getKeyAsString()));
            ParsedStringTerms catelogNameAgg = bucket.getAggregations().get("catelogNameAgg");
            catalogVo.setCatalogName(catelogNameAgg.getBuckets().get(0).getKeyAsString());
            catalogs.add(catalogVo);
        }
        //4.3 ??????????????????
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

        //5.????????????????????????????????????
        if(searchParam.getAttrs()!=null && !searchParam.getAttrs().isEmpty()){
        List<SearchResult.NavVo> navVos = searchParam.getAttrs().stream().map(attr -> {
            SearchResult.NavVo navVo = new SearchResult.NavVo();
            String[] s = attr.split("_");
            R r = productFeignSerivce.getAttrInfoById(Long.parseLong(s[0]));
            if(r.getCode()==0){
                AttrInfoResp attrInfo = r.getData("attr", new TypeReference<AttrInfoResp>(){});
                navVo.setName(attrInfo.getAttrName());
            }else{
                navVo.setName(s[0]);
            }
            navVo.setNavValue(s[1]);
            //??????????????? ????????????????????????
            String queryString = searchParam.get_queryString();
            String decode = null;
            try {
                decode = URLEncoder.encode(attr, "UTF-8"); //????????????URL

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String replace1 = decode.replace("+", "%20");
            String replace = queryString.replace("attrs=" + replace1, "");
            navVo.setLink("http://search.gulimall.com/list.html?"+replace);

            return navVo;
        }).collect(Collectors.toList());
            searchResult.setNavs(navVos);
        }


        return searchResult;
    }

    /**
     * ??????????????????????????????Request
     * @param searchParam ????????????
     * @return
     */
    private SearchRequest buildSearchProductRequest(SearchParam searchParam) {

        //1.??????????????????
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        searchSourceBuilder.query(boolQueryBuilder);
        //2.???????????????????????????
        if(!StringUtils.isEmpty(searchParam.getKeyword())){
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle",searchParam.getKeyword()));
        }
        //3.????????????????????????
        if(!StringUtils.isEmpty(searchParam.getCatalog3Id())){
            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId",searchParam.getCatalog3Id()));
        }
        //4.??????????????????
        if(!StringUtils.isEmpty(searchParam.getBrandId())){
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId",searchParam.getBrandId()));
        }
        // 5.??????????????????
        if(searchParam.getAttrs()!=null&&searchParam.getAttrs().size()>0){
            List<String> attrs = searchParam.getAttrs();
            //??????????????????,??????????????????nested???????????????
            attrs.forEach(attr->{
                //???????????????????????????attrs=1_5???:8???&2_16G:8G
                String[] s = attr.split("_");
                String[] attrValues = s[1].split(":");
                BoolQueryBuilder query = QueryBuilders.boolQuery();
                query.filter(QueryBuilders.termQuery("attrs.attrId",s[0]));
                query.filter(QueryBuilders.termsQuery("attrs.attrValue",attrValues));
                boolQueryBuilder.filter(QueryBuilders.nestedQuery("attrs", query, ScoreMode.None));
            });
        }

        //6.??????????????????
        if(searchParam.getHasStock()!=null){
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock",searchParam.getHasStock()==1));
        }
        //7.????????????????????????
        if(!StringUtils.isEmpty(searchParam.getSkuPrice())){
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("skuPrice");
            String[] range = searchParam.getSkuPrice().split("_");
            if(range.length==2&&!StringUtils.isEmpty(range[0])&&!StringUtils.isEmpty(range[1])){
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
        //8.????????????
        Integer pageNum = searchParam.getPageNum();
        searchSourceBuilder.from((pageNum-1)*EsConst.SEARCH_PAGE_SIZE);
        searchSourceBuilder.size(EsConst.SEARCH_PAGE_SIZE);
        //9.????????????
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<b style='color:red'>");
        highlightBuilder.postTags("</b>");
        highlightBuilder.field("skuTitle");
        searchSourceBuilder.highlighter(highlightBuilder);
        //10.????????????
        //10.1.????????????
        TermsAggregationBuilder brandAgg = AggregationBuilders.terms("BrandAggs").field("brandId").size(50);
        brandAgg.subAggregation(AggregationBuilders.terms("BrandNameAgg").field("brandName").size(1));
        brandAgg.subAggregation(AggregationBuilders.terms("BrandImgAgg").field("brandImg").size(1));
        searchSourceBuilder.aggregation(brandAgg);
        //10.2 ????????????
        TermsAggregationBuilder categoryAgg = AggregationBuilders.terms("categoryAggs").field("catalogId").size(50);
        categoryAgg.subAggregation(AggregationBuilders.terms("catelogNameAgg").field("catalogName").size(1));
        searchSourceBuilder.aggregation(categoryAgg);
        //10.3 ????????????
        NestedAggregationBuilder attrAggs = AggregationBuilders.nested("attrAggs", "attrs");
        TermsAggregationBuilder attrIdAggs = AggregationBuilders.terms("attrIdAggs").field("attrs.attrId").size(10);
        attrIdAggs.subAggregation(AggregationBuilders.terms("attrNameAggs").field("attrs.attrName").size(10));
        attrIdAggs.subAggregation(AggregationBuilders.terms("attrValueAggs").field("attrs.attrValue").size(10));
        attrAggs.subAggregation(attrIdAggs);
        searchSourceBuilder.aggregation(attrAggs);

        //11.????????????
        String sort = searchParam.getSort();
        if(!StringUtils.isEmpty(sort)){
            String[] s = sort.split("_");
            searchSourceBuilder.sort(s[0],"desc".equals(s[1])? SortOrder.DESC:SortOrder.ASC);
        }

        SearchRequest searchRequest = new SearchRequest(new String[]{EsConst.ELASTICSEARCH_INDEX_NAME},searchSourceBuilder);
        String dsl = searchRequest.source().toString();
        log.info("???????????????DSL??????={}",dsl);
        return searchRequest;
    }
}
