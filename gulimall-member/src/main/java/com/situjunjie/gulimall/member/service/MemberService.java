package com.situjunjie.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.situjunjie.common.utils.PageUtils;
import com.situjunjie.gulimall.member.entity.MemberEntity;
import com.situjunjie.gulimall.member.exception.UsernameExistsException;
import com.situjunjie.gulimall.member.vo.MemberLoginVo;
import com.situjunjie.gulimall.member.vo.MemberRegistVo;
import com.situjunjie.gulimall.member.vo.WeiboAccessTokenVo;

import java.util.Map;

/**
 * 会员
 *
 * @author situjunjie
 * @email situjunjie@vip.qq.com
 * @date 2021-06-16 13:32:11
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 会员注册接口
     * @param vo
     */
    void regist(MemberRegistVo vo);

    void checkUsernameExists(String username) throws UsernameExistsException;

    MemberEntity memberLogin(MemberLoginVo vo);

    MemberEntity weiboLogin(WeiboAccessTokenVo weiboAccessTokenVo) throws Exception;
}

