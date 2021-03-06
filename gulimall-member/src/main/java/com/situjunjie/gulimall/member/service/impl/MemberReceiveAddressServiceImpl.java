package com.situjunjie.gulimall.member.service.impl;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.situjunjie.common.utils.PageUtils;
import com.situjunjie.common.utils.Query;

import com.situjunjie.gulimall.member.dao.MemberReceiveAddressDao;
import com.situjunjie.gulimall.member.entity.MemberReceiveAddressEntity;
import com.situjunjie.gulimall.member.service.MemberReceiveAddressService;


@Service("memberReceiveAddressService")
public class MemberReceiveAddressServiceImpl extends ServiceImpl<MemberReceiveAddressDao, MemberReceiveAddressEntity> implements MemberReceiveAddressService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberReceiveAddressEntity> page = this.page(
                new Query<MemberReceiveAddressEntity>().getPage(params),
                new QueryWrapper<MemberReceiveAddressEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<MemberReceiveAddressEntity> getByMemberId(Long id) {
        return this.baseMapper.selectList(new QueryWrapper<MemberReceiveAddressEntity>().eq("member_id",id));
    }

    @Override
    public BigDecimal calFareByAddrId(Long addrId) {
        MemberReceiveAddressEntity addressEntity = getById(addrId);
        String phone = addressEntity.getPhone();
        String fare = phone.substring(phone.length() - 2, phone.length() - 1);
        return new BigDecimal(fare);
    }

}