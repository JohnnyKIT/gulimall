package com.situjunjie.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category2Vo {

    private String catalog1Id;
    private String id;
    private String name;
    private List<Category3Vo> catalog3List;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Category3Vo{
        private String catalog2Id;
        private String id;
        private String name;
    }
}
