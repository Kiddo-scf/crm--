package com.fs.crm.workbench.web.controller;

import com.fs.crm.settings.domain.User;
import com.fs.crm.settings.service.UserService;
import com.fs.crm.settings.service.impl.UserServiceImpl;
import com.fs.crm.utils.DateTimeUtil;
import com.fs.crm.utils.PrintJson;
import com.fs.crm.utils.ServiceFactory;
import com.fs.crm.utils.UUIDUtil;
import com.fs.crm.workbench.domain.Activity;
import com.fs.crm.workbench.domain.Clue;
import com.fs.crm.workbench.domain.Tran;
import com.fs.crm.workbench.domain.TranHistory;
import com.fs.crm.workbench.service.ActivityService;
import com.fs.crm.workbench.service.ClueService;
import com.fs.crm.workbench.service.CustomerService;
import com.fs.crm.workbench.service.TranService;
import com.fs.crm.workbench.service.impl.ActivityServiceImpl;
import com.fs.crm.workbench.service.impl.ClueServiceImpl;
import com.fs.crm.workbench.service.impl.CustomerServiceImpl;
import com.fs.crm.workbench.service.impl.TranServiceImpl;
import javafx.scene.SnapshotParametersBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranController extends HttpServlet {
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到交易控制器");
        //response.setContentType("text/html;charset=utf-8");

        String path = request.getServletPath();//获取访问路径

        if ("/workbench/transaction/add.do".equals(path)) {
            add(request,response);

        } else if ("/workbench/transaction/getCustomerName.do".equals(path)) {
            getCustomerName(request,response);
        }else if ("/workbench/transaction/save.do".equals(path)) {
            save(request,response);
        }else if ("/workbench/transaction/detail.do".equals(path)) {
            detail(request,response);
        }else if ("/workbench/transaction/getHistoryListByTranId.do".equals(path)) {
            getHistoryListByTranId(request,response);
        }else if ("/workbench/transaction/changeStage.do".equals(path)) {
            changeStage(request,response);
        }else if ("/workbench/transaction/getCharts.do".equals(path)) {
            getCharts(request,response);
        }
    }

    private void getCharts(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("取得交易阶段数量统计图表的数据");

        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());
        Map<String,Object> map = tranService.getCharts();

        PrintJson.printJsonObj(response,map);



    }

    private void changeStage(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行改变阶段的操作");

        String id = request.getParameter("id");
        String stage = request.getParameter("stage");
        String money = request.getParameter("money");
        String expectedDate= request.getParameter("expectedDate");
        String editBy = ((User)request.getSession().getAttribute("user")).getName();
        String editTime = DateTimeUtil.getSysTime();

        System.out.println(stage+"======");
        System.out.println(money+"======");
        System.out.println(expectedDate+"======");
        System.out.println(id+"======");

        Tran tran = new Tran();
        tran.setId(id);
        tran.setStage(stage);
        tran.setMoney(money);
        tran.setExpectedDate(expectedDate);
        tran.setEditBy(editBy);
        tran.setEditTime(editTime);

        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());
        boolean flag = tranService.changeStage(tran);

        Map<String,String> pMap = (Map<String,String>)request.getServletContext().getAttribute("pMap");
        String possibility = pMap.get(stage);
        tran.setPossibility(possibility);

        Map<String,Object> map = new HashMap<>();
        map.put("success",flag);
        map.put("tran",tran);

        PrintJson.printJsonObj(response,map);



    }

    private void getHistoryListByTranId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("根据交易id取得相应的历史列表");

        String tranId = request.getParameter("tranId");

        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());
        List<TranHistory> tranHistoryList = tranService.getHistoryListByTranId(tranId);

        //阶段和可能性之间的对应关系
        Map<String,String> pMap = (Map<String,String>)request.getServletContext().getAttribute("pMap");

        for (TranHistory tranHistory : tranHistoryList) {

            //获取阶段
            String stage = tranHistory.getStage();
            //获取可能性
            String possibility = pMap.get(stage);

            tranHistory.setPossibility(possibility);
        }

        PrintJson.printJsonObj(response,tranHistoryList);

    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("跳转到详细信息页");

        String id = request.getParameter("id");

        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());
        Tran tran = tranService.detail(id);

        //处理可能性
        String stage = tran.getStage();
        Map<String,String> pMap = (Map<String,String>)request.getServletContext().getAttribute("pMap");
        String possibility = pMap.get(stage);
        tran.setPossibility(possibility);

        request.setAttribute("tran",tran);
        request.getRequestDispatcher("/workbench/transaction/detail.jsp").forward(request,response);

    }

    private void save(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("执行添加交易的操作");

        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String money = request.getParameter("money");
        String name = request.getParameter("name");
        String expectedDate = request.getParameter("expectedDate");
        String customerName = request.getParameter("customerName");
        String stage = request.getParameter("stage");
        String type = request.getParameter("type");
        String source = request.getParameter("source");
        String activityId = request.getParameter("activityId");
        String contactsId = request.getParameter("contactsId");
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");

        Tran tran = new Tran();
        tran.setId(id);
        tran.setOwner(owner);
        tran.setMoney(money);
        tran.setName(name);
        tran.setExpectedDate(expectedDate);
        tran.setStage(stage);
        tran.setType(type);
        tran.setSource(source);
        tran.setActivityId(activityId);
        tran.setContactsId(contactsId);
        tran.setCreateTime(createTime);
        tran.setCreateBy(createBy);
        tran.setDescription(description);
        tran.setContactSummary(contactSummary);
        tran.setNextContactTime(nextContactTime);

        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());
        boolean flag = tranService.save(tran,customerName);
        if(flag){
            //如果交易添加成功,跳转到列表页
            response.sendRedirect(request.getContextPath()+"/workbench/clue/index.jsp");

        }



    }

    private void getCustomerName(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("取得客户名称列表(按照客户名称进行模糊查询)");

        String name = request.getParameter("name");

        CustomerService customerService = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());

        List<String> stringList = customerService.getCustomerName(name);

        PrintJson.printJsonObj(response,stringList);
    }

    private void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到跳转到交易添加页的操作");

        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> userList = userService.getUserList();
        request.setAttribute("userList",userList);

        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request,response);

    }
}
