package com.situjunjie.common.to;

import lombok.Data;

/**
 * 用于查询用户订单和订单项的请求Vo
 */
@Data
public class MemberOrderReqTo {

    private Long memberId;

    private String pageNum;

}
