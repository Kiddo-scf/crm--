package com.fs.crm.settings.web.controller;

import com.fs.crm.settings.domain.User;
import com.fs.crm.settings.service.UserService;
import com.fs.crm.settings.service.impl.UserServiceImpl;
import com.fs.crm.utils.MD5Util;
import com.fs.crm.utils.PrintJson;
import com.fs.crm.utils.ServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserController extends HttpServlet {
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到用户控制器");
        //response.setContentType("text/html;charset=utf-8");

        String path = request.getServletPath();//获取访问路径

        if("/settings/user/login.do".equals(path)){
            login(request,response);
        }else {

        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到验证登录操作");

        String loginAct = request.getParameter("loginAct");
        String loginPwd = request.getParameter("loginPwd");
        //将明文密码加密成密文
        loginPwd = MD5Util.getMD5(loginPwd);
        //获取客户端ip
        String ip = request.getRemoteAddr();

        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());

        try{
            User user = userService.login(loginAct,loginPwd,ip);
            request.getSession().setAttribute("user",user);//若业务层没有为controller层抛出异常,可执行到此处
            PrintJson.printJsonFlag(response,true);

        }catch (Exception e){
            //业务层抛出异常，登录失败
            e.printStackTrace();
            String msg = e.getMessage();
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("success",false);
            map.put("msg",msg);
            PrintJson.printJsonObj(response,map);
        }

    }
//
}
