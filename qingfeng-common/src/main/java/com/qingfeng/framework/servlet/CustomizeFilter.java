package com.qingfeng.framework.servlet;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author Administrator
 * @version 1.0.0
 * @ProjectName qingfeng
 * @Description 过滤器
 * @createTime 2022年01月18日 13:18:00
 */
public class CustomizeFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        long start = System.currentTimeMillis();
        filterChain.doFilter(servletRequest,servletResponse);
//        System.out.println("Execute cost="+(System.currentTimeMillis()-start));
    }

    public void destroy() {

    }
}