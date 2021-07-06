package com.situjunjie.gulimall.gulimallsearch.service;

import com.situjunjie.gulimall.gulimallsearch.vo.SearchParam;
import com.situjunjie.gulimall.gulimallsearch.vo.SearchResult;

public interface MallSearchService {
    SearchResult search(SearchParam searchParam);
}
