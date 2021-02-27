package com.fs.crm.workbench.web.controller;

import com.fs.crm.settings.domain.User;
import com.fs.crm.settings.service.UserService;
import com.fs.crm.settings.service.impl.UserServiceImpl;
import com.fs.crm.utils.DateTimeUtil;
import com.fs.crm.utils.PrintJson;
import com.fs.crm.utils.ServiceFactory;
import com.fs.crm.utils.UUIDUtil;
import com.fs.crm.vo.PaginationVO;
import com.fs.crm.workbench.domain.Activity;
import com.fs.crm.workbench.domain.ActivityRemark;
import com.fs.crm.workbench.domain.Clue;
import com.fs.crm.workbench.domain.Tran;
import com.fs.crm.workbench.service.ActivityService;
import com.fs.crm.workbench.service.ClueService;
import com.fs.crm.workbench.service.impl.ActivityServiceImpl;
import com.fs.crm.workbench.service.impl.ClueServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClueController extends HttpServlet {
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到线索控制器");
        //response.setContentType("text/html;charset=utf-8");

        String path = request.getServletPath();//获取访问路径

        if("/workbench/clue/getUserList.do".equals(path)){
            getUserList(request,response);

        }else if("/workbench/clue/save.do".equals(path)){
            save(request,response);
        }else if("/workbench/clue/detail.do".equals(path)){
            detail(request,response);
        }else if("/workbench/clue/getActivityListByClueId.do".equals(path)){
            getActivityListByClueId(request,response);
        }else if("/workbench/clue/unbind.do".equals(path)){
            unbind(request,response);
        }else if("/workbench/clue/getActivityListByNameAndNotByClueId.do".equals(path)){
            getActivityListByNameAndNotByClueId(request,response);
        }else if("/workbench/clue/bind.do".equals(path)){
            bind(request,response);
        }else if("/workbench/clue/getActivityListByName.do".equals(path)){
            getActivityListByName(request,response);
        }else if("/workbench/clue/convert.do".equals(path)){
            convert(request,response);
        }

    }

    private void convert(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("执行线索转换的操作");

        String clueId = request.getParameter("clueId");
        String flag = request.getParameter("flag");
        Tran tran = null;

        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        //根据接收到的标记为决定是否创建交易
        if("a".equals(flag)){

            tran = new Tran();
            //接收交易表单中的参数
            String money = request.getParameter("money");
            String name = request.getParameter("name");
            String expectedDate = request.getParameter("expectedDate");
            String stage = request.getParameter("stage");
            String activityId = request.getParameter("activityId");
            String id = UUIDUtil.getUUID();
            String createTime = DateTimeUtil.getSysTime();


            tran.setActivityId(activityId);
            tran.setCreateTime(createTime);
            tran.setCreateBy(createBy);
            tran.setMoney(money);
            tran.setExpectedDate(expectedDate);
            tran.setStage(stage);
            tran.setId(id);
            tran.setName(name);

        }
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag1 = clueService.convert(clueId,tran,createBy);

        if(flag1){

            response.sendRedirect(request.getContextPath()+"/workbench/clue/index.jsp");

        }

    }

    private void getActivityListByName(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("查询市场活动列表(根据名称模糊查询)");

        String aname = request.getParameter("aname");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> activityList = activityService.getActivityListByName(aname);

        PrintJson.printJsonObj(response,activityList);

    }

    private void bind(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行市场活动关联操作");

        String cid = request.getParameter("cid");
        String[] aids = request.getParameterValues("aid");

        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = clueService.bind(cid,aids);

        PrintJson.printJsonFlag(response,flag);
    }

    private void getActivityListByNameAndNotByClueId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("查询市场活动列表(根据名称模糊查询+排除掉已经关联指定线索的列表)");

        String aname = request.getParameter("aname");
        String clueId = request.getParameter("clueId");

        Map<String,String> map = new HashMap<>();
        map.put("aname",aname);
        map.put("clueId",clueId);

        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> activityList = activityService.getActivityListByNameAndNotByClueId(map);

        PrintJson.printJsonObj(response,activityList);

    }

    private void unbind(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行接触市场活动和线索关联的操作");

        String id = request.getParameter("id");
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = clueService.unbind(id);

        PrintJson.printJsonFlag(response,flag);

    }

    private void getActivityListByClueId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("根据线索id查询关联的市场活动列表");

        String clueId = request.getParameter("clueId");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> activityList = activityService.getActivityListByClueId(clueId);

        PrintJson.printJsonObj(response,activityList);

    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("跳转到线索详细信息页");

        String id = request.getParameter("id");
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        Clue clue = clueService.getClueById(id);
        request.setAttribute("clue",clue);
        request.getRequestDispatcher("/workbench/clue/detail.jsp").forward(request,response);
    }

    private void save(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行添加线索操作");

        String id = UUIDUtil.getUUID();
        String fullname = request.getParameter("fullname");
        String appellation = request.getParameter("appellation");
        String owner = request.getParameter("owner");
        String company = request.getParameter("company");
        String job = request.getParameter("job");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String website = request.getParameter("website");
        String mphone = request.getParameter("mphone");
        String state = request.getParameter("state");
        String source = request.getParameter("source");
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        String createTime = DateTimeUtil.getSysTime();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");
        String address = request.getParameter("address");

        Clue clue = new Clue();
        clue.setWebsite(website);
        clue.setAddress(address);
        clue.setAppellation(appellation);
        clue.setCompany(company);
        clue.setContactSummary(contactSummary);
        clue.setCreateBy(createBy);
        clue.setCreateTime(createTime);
        clue.setDescription(description);
        clue.setEmail(email);
        clue.setId(id);
        clue.setFullname(fullname);
        clue.setJob(job);
        clue.setMphone(mphone);
        clue.setNextContactTime(nextContactTime);
        clue.setOwner(owner);
        clue.setPhone(phone);
        clue.setSource(source);
        clue.setState(state);

        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = clueService.save(clue);
        PrintJson.printJsonFlag(response,flag);

    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("取得用户信息列表");

        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());

        List<User> userList = userService.getUserList();

        PrintJson.printJsonObj(response,userList);
    }


}
