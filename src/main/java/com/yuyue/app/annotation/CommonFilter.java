package com.yuyue.app.annotation;


import com.github.pagehelper.PageHelper;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@WebFilter()
public class CommonFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
 // TODO Auto-generated method stub
}

/* (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)throws IOException, ServletException {
        chain.doFilter(request, response);
        //清空pagehelper
        PageHelper.clearPage();
    }

    /* (non-Javadoc)
         * @see javax.servlet.Filter#destroy()
   */
    @Override
    public void destroy() {}

}