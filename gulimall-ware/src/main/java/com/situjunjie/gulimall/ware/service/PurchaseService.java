package com.situjunjie.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.situjunjie.common.utils.PageUtils;
import com.situjunjie.gulimall.ware.entity.PurchaseEntity;
import com.situjunjie.gulimall.ware.vo.MergeVo;
import com.situjunjie.gulimall.ware.vo.PurchaseDoneVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author situjunjie
 * @email situjunjie@vip.qq.com
 * @date 2021-06-16 13:33:09
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnrecive(Map<String, Object> params);

    void mergePurchaseDetail(MergeVo mergeVo);

    void received(List<Long> ids);

    void done(PurchaseDoneVo doneVo);
}

