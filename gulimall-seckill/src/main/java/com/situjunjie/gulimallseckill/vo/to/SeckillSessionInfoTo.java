package com.situjunjie.gulimallseckill.vo.to;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.situjunjie.gulimallseckill.vo.SeckillSkuRelationEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SeckillSessionInfoTo {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;
    /**
     * 场次名称
     */
    private String name;
    /**
     * 每日开始时间
     */
    private Date startTime;
    /**
     * 每日结束时间
     */
    private Date endTime;
    /**
     * 启用状态
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;


    private List<SeckillSkuRelationEntity> relationSkus;


}
