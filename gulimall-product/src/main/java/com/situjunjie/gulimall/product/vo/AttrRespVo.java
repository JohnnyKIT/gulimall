package com.situjunjie.gulimall.product.vo;

import lombok.Data;

/**
 *
 * 返回属性的界面VO
 */
@Data
public class AttrRespVo extends AttrVo{

    /**
     * 所属分类
     */
    private String catelogName;

    /**
     * 所属分组
     */
    private String groupName;

    /**
     * 三级分类路径
     */
    private Long[] catelogPath;

}
