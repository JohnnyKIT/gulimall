package com.situjunjie.gulimall.member.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.situjunjie.gulimall.member.entity.MemberReceiveAddressEntity;
import com.situjunjie.gulimall.member.service.MemberReceiveAddressService;
import com.situjunjie.common.utils.PageUtils;
import com.situjunjie.common.utils.R;



/**
 * 会员收货地址
 *
 * @author situjunjie
 * @email situjunjie@vip.qq.com
 * @date 2021-06-16 13:32:11
 */
@RestController
@RequestMapping("member/memberreceiveaddress")
public class MemberReceiveAddressController {
    @Autowired
    private MemberReceiveAddressService memberReceiveAddressService;

    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("member:memberreceiveaddress:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberReceiveAddressService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
   // @RequiresPermissions("member:memberreceiveaddress:info")
    public R info(@PathVariable("id") Long id){
		MemberReceiveAddressEntity memberReceiveAddress = memberReceiveAddressService.getById(id);

        return R.ok().put("memberReceiveAddress", memberReceiveAddress);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("member:memberreceiveaddress:save")
    public R save(@RequestBody MemberReceiveAddressEntity memberReceiveAddress){
		memberReceiveAddressService.save(memberReceiveAddress);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:memberreceiveaddress:update")
    public R update(@RequestBody MemberReceiveAddressEntity memberReceiveAddress){
		memberReceiveAddressService.updateById(memberReceiveAddress);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:memberreceiveaddress:delete")
    public R delete(@RequestBody Long[] ids){
		memberReceiveAddressService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @ResponseBody
    @GetMapping("/{memberId}")
    public R getMemberReceiveAddress(@PathVariable("memberId") Long id){
        List<MemberReceiveAddressEntity> address = memberReceiveAddressService.getByMemberId(id);
        return R.ok().put("address",address);
    }

    @ResponseBody
    @GetMapping("/calFare/{addrId}")
    public R calFareByAddrId(@PathVariable("addrId") Long addrId){
        BigDecimal fare = memberReceiveAddressService.calFareByAddrId(addrId);
        MemberReceiveAddressEntity selectedAddr = memberReceiveAddressService.getById(addrId);
        return R.ok().put("fare",fare).put("selectedAddr",selectedAddr);
    }

}
