package com.situjunjie.gulimall.gulimallcart.interceptor;

import com.situjunjie.common.constant.AuthServerConst;
import com.situjunjie.common.constant.CartConst;
import com.situjunjie.common.to.MemberEntity;
import com.situjunjie.gulimall.gulimallcart.vo.UserInfoTo;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

public class CartInterceptor implements HandlerInterceptor {


    public static ThreadLocal<UserInfoTo> threadLocal = new InheritableThreadLocal<>();
    /**
     * 业务处理前拦截，主要是生成购物车用户对象
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserInfoTo userInfoTo = new UserInfoTo();
        HttpSession session = request.getSession();
        MemberEntity memberEntity = (MemberEntity) session.getAttribute(AuthServerConst.LOGIN_USER_SESSION);
        if(memberEntity!=null){
            //用户已经登录
            userInfoTo.setUsername(memberEntity.getUsername());
            userInfoTo.setUserId(memberEntity.getId());
        }
        Cookie[] cookies = request.getCookies();
        if(cookies!=null && cookies.length>0){
            for (Cookie cookie : cookies) {
                if(CartConst.TEMP_USER_COOKIE_NAME.equals(cookie.getName())){
                    userInfoTo.setUserKey(cookie.getValue());
                    userInfoTo.setTempUser(false);
                }
            }
        }
        if(StringUtils.isEmpty(userInfoTo.getUserKey())){
            //如果没有分配过user-key就分配一个吧
            String userKey = UUID.randomUUID().toString();
            userInfoTo.setUserKey(userKey);
        }

        threadLocal.set(userInfoTo); //保存到当前线程对象中
        return true;
    }


    /**
     * 业务执行完后，user-key要交给浏览器保存cookie
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserInfoTo userInfoTo = threadLocal.get();
        Cookie cookie = new Cookie(CartConst.TEMP_USER_COOKIE_NAME,userInfoTo.getUserKey());
        cookie.setMaxAge(CartConst.TEMP_USER_COOKIE_TIMEOUT);
        cookie.setDomain("gulimall.com");
        response.addCookie(cookie);
    }
}
