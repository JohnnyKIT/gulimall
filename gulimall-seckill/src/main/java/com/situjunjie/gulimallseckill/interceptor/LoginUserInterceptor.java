package com.situjunjie.gulimallseckill.interceptor;

import com.situjunjie.common.to.MemberEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    /**
     * 跳转到订单确认页面需要确定登录状态
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */


    public static ThreadLocal<MemberEntity> threadLocal = new InheritableThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String uri = request.getRequestURI();
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        //过滤部分不需要跳转到登录页的请求
        boolean match = antPathMatcher.match("/kill/**", uri); //秒杀请求
        if (match){
            MemberEntity memberInfo = (MemberEntity) request.getSession().getAttribute("memberInfo");
            if(memberInfo!=null){
                threadLocal.set(memberInfo);
                return true;
            }else{
                //没登录则跳回登录页面
                response.sendRedirect("http://auth.gulimall.com/login.html");
                return false;
            }
        }
        return true;

    }
}
