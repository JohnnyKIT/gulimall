package com.situjunjie.gulimall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.situjunjie.common.utils.HttpUtils;
import com.situjunjie.gulimall.member.exception.UsernameExistsException;
import com.situjunjie.gulimall.member.vo.MemberLoginVo;
import com.situjunjie.gulimall.member.vo.MemberRegistVo;
import com.situjunjie.gulimall.member.vo.WeiboAccessTokenVo;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.situjunjie.common.utils.PageUtils;
import com.situjunjie.common.utils.Query;

import com.situjunjie.gulimall.member.dao.MemberDao;
import com.situjunjie.gulimall.member.entity.MemberEntity;
import com.situjunjie.gulimall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void regist(MemberRegistVo vo) {
        //1.entity属性装配
        MemberEntity entity = new MemberEntity();
        entity.setUsername(vo.getUsername());
        entity.setMobile(vo.getPhone());
        // 密码应该修改成加密保存
        String password = vo.getPassword();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode(password);
        entity.setPassword(encode);
        //2.校验entity数据
        checkUsernameExists(entity.getUsername());
        //3.装配会员默认信息
        entity.setLevelId(1l);

        //4.保存会员信息
        baseMapper.insert(entity);
    }

    public void checkUsernameExists(String username) throws UsernameExistsException{
        Integer count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", username));
        if(count>0){
            throw new UsernameExistsException();
        }
    }

    @Override
    public MemberEntity memberLogin(MemberLoginVo vo) {
        QueryWrapper<MemberEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("username",vo.getLoginacct()).or().eq("mobile",vo.getLoginacct());
        MemberEntity entity = baseMapper.selectOne(wrapper);
        if(entity==null){
            return null;
        }
        //对比校验密码
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean matches = encoder.matches(vo.getPassword(), entity.getPassword());
        if(matches){
            return entity;
        }

        return null;

    }

    /**
     * 微博登录实现  包含第一次注册逻辑
     * @param weiboAccessTokenVo
     * @return
     */
    @Override
    public MemberEntity weiboLogin(WeiboAccessTokenVo weiboAccessTokenVo) throws Exception {

        //判断是否在数据库中
        MemberEntity member = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", weiboAccessTokenVo.getUid()));
        if(member==null){
            //1.第一次社交登录，要包含注册逻辑
            //查询详细社交信息
            Map<String, String> query = new HashMap<>();
            query.put("access_token",weiboAccessTokenVo.getAccess_token());
            query.put("uid",weiboAccessTokenVo.getUid());
            HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<>(), query);
            String json = EntityUtils.toString(response.getEntity());
            JSONObject jsonObject = JSON.parseObject(json);
            //装配memberRegist进行注册
            MemberEntity memberRegist = new MemberEntity();
            memberRegist.setAccessToken(weiboAccessTokenVo.getAccess_token());
            memberRegist.setExpiresIn(String.valueOf(weiboAccessTokenVo.getExpires_in()));
            memberRegist.setSocialUid(weiboAccessTokenVo.getUid());
            memberRegist.setNickname(jsonObject.getString("name"));
            memberRegist.setCity(jsonObject.getString("location"));
            memberRegist.setGender("m".equals(jsonObject.getString("gender"))?1:0);
            baseMapper.insert(memberRegist);
            return memberRegist;
        }else{
            //该社交账号之前已经登录过了
            member.setAccessToken(weiboAccessTokenVo.getAccess_token());
            return member;
        }

    }


}