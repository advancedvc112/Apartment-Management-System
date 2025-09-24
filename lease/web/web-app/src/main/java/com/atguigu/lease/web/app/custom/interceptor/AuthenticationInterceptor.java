package com.atguigu.lease.web.app.custom.interceptor;

import com.atguigu.lease.common.context.LoginUser;
import com.atguigu.lease.common.context.LoginUserContext;
import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthenticationInterceptor implements HandlerInterceptor {
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("access_token");
        if (token == null) {
            throw new LeaseException(ResultCodeEnum.ADMIN_LOGIN_AUTH);
        }else{
            Claims claims = JwtUtil.parseToken(token);
            Long userId = claims.get("userId", Long.class);
            String userName = claims.get("username", String.class);
            LoginUserContext.setLoginUser(new LoginUser(userId, userName));
        }
        return true;
    }

    public void afterCompletion(final HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        LoginUserContext.clear();
    }
}
