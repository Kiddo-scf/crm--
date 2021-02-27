package com.fs.crm.web.filter;

import com.fs.crm.settings.domain.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

        System.out.println("进入登录拦截器");

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String path = request.getServletPath();

        //不应拦截的资源，自动放行
        if("/login.jsp".equals(path) || "/settings/user/login.do".equals(path)){

            chain.doFilter(req,resp);

        }else {//未登录过,重定向回登录页进行登录
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            if(user!=null){
                chain.doFilter(req,resp);
            }else {
                response.sendRedirect(request.getContextPath()+"/login.jsp");
            }
        }



    }
}
