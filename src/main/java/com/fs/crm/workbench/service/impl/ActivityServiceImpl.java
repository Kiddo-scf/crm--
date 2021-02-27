package com.fs.crm.workbench.service.impl;

import com.fs.crm.settings.dao.UserDao;
import com.fs.crm.settings.domain.User;
import com.fs.crm.utils.SqlSessionUtil;
import com.fs.crm.vo.PaginationVO;
import com.fs.crm.workbench.dao.ActivityDao;
import com.fs.crm.workbench.dao.ActivityRemarkDao;
import com.fs.crm.workbench.domain.Activity;
import com.fs.crm.workbench.domain.ActivityRemark;
import com.fs.crm.workbench.service.ActivityService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityServiceImpl implements ActivityService {

    ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);
    ActivityRemarkDao activityRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ActivityRemarkDao.class);
    UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    @Override
    public boolean save(Activity activity) {
        boolean flag = true;
        int count = activityDao.save(activity);
        if(count!=1){
            flag = false;
        }
        return flag;
    }

    @Override
    public PaginationVO<Activity> pageList(Map<String, Object> map) {
        System.out.println("===========");
        //取得total
        int total = activityDao.getTotalByCondition(map);

        //取得dataList
        List<Activity> dataList = activityDao.getActivityListByCondition(map);

        //创建一个vo对象，封装total和dataList
        PaginationVO<Activity> vo = new PaginationVO<>();
        vo.setTotal(total);
        vo.setDataList(dataList);

        return vo;
    }

    @Override
    public boolean delete(String[] ids) {
        boolean flag = true;

        //查询出需要删除的备注记录的数量
        int count1 = activityRemarkDao.getCountByAids(ids);

        //删除备注，返回受影响的条数(实际删除的数量)
        int count2 = activityRemarkDao.deleteByAids(ids);

        if(count1 != count2){

            flag = false;

        }

        //删除市场活动
        int count3 = activityDao.delete(ids);
        if(count3!=ids.length){

            flag = false;

        }

        return flag;
    }

    @Override
    public Map<String, Object> getUserListAndActivity(String id) {
        //取用户列表
        List<User> userList = userDao.getUserList();

        //取单条activity
        Activity activity = activityDao.getById(id);

        //打包成map
        Map<String, Object> map = new HashMap<>();
        map.put("userList",userList);
        map.put("activity",activity);

        //返回map
        return map;
    }

    @Override
    public boolean update(Activity activity) {

        boolean flag = true;
        int count = activityDao.update(activity);
        if(count!=1){
            flag = false;
        }

        return flag;
    }

    @Override
    public Activity detail(String id) {

        Activity activity = activityDao.detail(id);

        return activity;
    }

    @Override
    public List<ActivityRemark> getRemarkListByAid(String activityId) {

        List<ActivityRemark> activityRemarkList = activityRemarkDao.getRemarkListByAid(activityId);

        return activityRemarkList;
    }

    @Override
    public boolean deleteRemark(String id) {

        boolean flag = true;

        int count = activityRemarkDao.deleteById(id);

        if(count!=1){
            flag = false;
        }

        return flag;
    }

    @Override
    public boolean saveRemark(ActivityRemark activityRemark) {

        boolean flag = true;
        int count = activityRemarkDao.saveRemark(activityRemark);

        if(count!=1){
            flag = false;
        }

        return flag;
    }

    @Override
    public boolean updateRemark(ActivityRemark activityRemark) {
        boolean flag = true;

        int count = activityRemarkDao.updateRemark(activityRemark);

        if(count!=1){
            flag = false;
        }

        return flag;
    }

    @Override
    public List<Activity> getActivityListByClueId(String clueId) {
        List<Activity> activityList = activityDao.getActivityListByClueId(clueId);

        return activityList;
    }

    @Override
    public List<Activity> getActivityListByNameAndNotByClueId(Map<String, String> map) {
        List<Activity> activityList = activityDao.getActivityListByNameAndNotByClueId(map);

        return activityList;
    }

    @Override
    public List<Activity> getActivityListByName(String aname) {
        List<Activity> activityList = activityDao.getActivityListByName(aname);

        return activityList;
    }

}
