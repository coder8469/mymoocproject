package com.mall.filter;

import com.mall.common.Const;
import com.mall.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by Administrator on 2017-5-10.
 */
public class ReqFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(ReqFilter.class);
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
             HttpServletResponse httpServletResponse, FilterChain filterChain)
                    throws ServletException, IOException {
        String uri = httpServletRequest.getRequestURI();
        logger.info("uri:{}",uri);
        HttpSession session = httpServletRequest.getSession();
        if (uri.contains("login.do")) {
            User user = (User) session.getAttribute(Const.CURRENT_USER);
            if (user != null) {
                logger.info("用户：{} 已经登录！", user.getUsername());
//                filterChain.doFilter(httpServletRequest, httpServletResponse);
            } else {
                logger.info("用户未登录，放行");
                filterChain.doFilter(httpServletRequest, httpServletResponse);
                logger.info("已放行");
            }
        } else {
            logger.info("用户未登录，放行");
            filterChain.doFilter(httpServletRequest,httpServletResponse);
        }
    }
}
