package com.situjunjie.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

@Data
public class MergeVo {
//    items: [1, 2]
//    purchaseId: 5
    private Long purchaseId;

    private List<Long> items;
}
