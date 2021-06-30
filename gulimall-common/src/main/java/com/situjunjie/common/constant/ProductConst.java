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

    public enum StatusEnum{
        NEW_SPU(0,"商品新建"),SPU_UP(1,"商品上架"),SPU_DOWN(2,"商品下架");

        private Integer code;
        private String msg;
        StatusEnum(Integer code,String msg){
            this.code = code ;
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
