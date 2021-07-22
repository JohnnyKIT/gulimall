package com.situjunjie.gulimall.member.controller;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.situjunjie.common.constant.AuthServerConst;
import com.situjunjie.common.exception.BizCodeEnum;
import com.situjunjie.gulimall.member.exception.UsernameExistsException;
import com.situjunjie.gulimall.member.service.feign.CouponFeign;
import com.situjunjie.gulimall.member.vo.MemberLoginVo;
import com.situjunjie.gulimall.member.vo.MemberRegistVo;
import com.situjunjie.gulimall.member.vo.WeiboAccessTokenVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.situjunjie.gulimall.member.entity.MemberEntity;
import com.situjunjie.gulimall.member.service.MemberService;
import com.situjunjie.common.utils.PageUtils;
import com.situjunjie.common.utils.R;

import javax.servlet.http.HttpSession;


/**
 * 会员
 *
 * @author situjunjie
 * @email situjunjie@vip.qq.com
 * @date 2021-06-16 13:32:11
 */

@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    private CouponFeign couponFeign;

    /**
     *
     * 测试feign远程调用
     */
    @RequestMapping("/testfeign/list")
    public R testFeign(){
        R couponR = couponFeign.testcoupon();
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("张三");
        return R.ok().put("member",memberEntity).put("coupon",couponR.get("coupon"));
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
   // @RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 会员注册
     */
    @RequestMapping("/regist")
    public R memberRegist(@RequestBody MemberRegistVo vo){

        try {
            memberService.regist(vo);
        } catch (UsernameExistsException e){
            return R.error(BizCodeEnum.USERNAME_EXISTS_EXCETION.getCode(),BizCodeEnum.USERNAME_EXISTS_EXCETION.getMessage());
        }
        return R.ok();
    }
    /**
     * 会员登录
     */
    @PostMapping("/login")
    public R memberLogin(@RequestBody MemberLoginVo vo){

        MemberEntity entity = memberService.memberLogin(vo);
        if(entity==null){
            //没获取到对象即登录失败
            return R.error(BizCodeEnum.USERNAME_PASSWORD_INVALID_EXCEPTION.getCode(), BizCodeEnum.USERNAME_PASSWORD_INVALID_EXCEPTION.getMessage());
        }

        return R.ok().put(AuthServerConst.LOGIN_USER_SESSION,entity);
    }

    /**
     * 微博登录
     */
    @PostMapping("/weibo_login")
    public R weiboLogin(@RequestBody WeiboAccessTokenVo weiboAccessTokenVo) throws Exception {

        MemberEntity entity = memberService.weiboLogin(weiboAccessTokenVo);
        if(entity==null){
            return R.error();
        }
        return R.ok().put("memberInfo",entity);
    }

}
