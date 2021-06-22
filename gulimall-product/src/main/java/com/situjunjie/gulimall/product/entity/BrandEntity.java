package com.situjunjie.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.situjunjie.common.valid.AddGroup;
import com.situjunjie.common.valid.UpdateGroup;
import com.situjunjie.common.valid.ValidInList;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import javax.validation.constraints.*;

/**
 * 品牌
 * 
 * @author situjunjie
 * @email situjunjie@vip.qq.com
 * @date 2021-06-16 13:30:30
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@TableId
	@NotNull(groups = {UpdateGroup.class})
	@Null(groups = {AddGroup.class})
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "品牌名不能为空",groups = {UpdateGroup.class,AddGroup.class})
	@NotEmpty(groups = {UpdateGroup.class,AddGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotEmpty(groups = {UpdateGroup.class,AddGroup.class})
	@URL(message = "品牌logo必须是一个合法的URL地址",groups = {UpdateGroup.class,AddGroup.class})
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */

	@ValidInList(groups = {UpdateGroup.class,AddGroup.class},message = "值必须为0、1",vals = {1,0})
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotEmpty(groups = {UpdateGroup.class,AddGroup.class})
	@Pattern(regexp = "^[a-zA-z]$",message = "检索首字母必须是一个字母",groups = {UpdateGroup.class,AddGroup.class})
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull(groups = {UpdateGroup.class,AddGroup.class})
	@Min(value = 0,message = "排序数必须是一个非负数",groups = {UpdateGroup.class,AddGroup.class})
	private Integer sort;

}
