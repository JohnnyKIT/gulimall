package com.situjunjie.common.constant;

public class ProductConst {

    public static final String ELASTICSEARCH_INDEX_NAME = "product";

    public enum AttrEnum {
        ATTR_TYPE_SELL(0,"销售属性"),ATTR_TYPE_BASE(1,"基本属性");

        private Integer code;
        private String msg;

        AttrEnum(Integer code,String msg){
            this.code = code;
            this.msg = msg;
        }

        public Integer getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
