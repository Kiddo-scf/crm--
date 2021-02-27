package com.fs.crm.workbench.service.impl;

import com.fs.crm.utils.DateTimeUtil;
import com.fs.crm.utils.SqlSessionUtil;
import com.fs.crm.utils.UUIDUtil;
import com.fs.crm.workbench.dao.CustomerDao;
import com.fs.crm.workbench.dao.TranDao;
import com.fs.crm.workbench.dao.TranHistoryDao;
import com.fs.crm.workbench.domain.Customer;
import com.fs.crm.workbench.domain.Tran;
import com.fs.crm.workbench.domain.TranHistory;
import com.fs.crm.workbench.service.TranService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranServiceImpl implements TranService {

    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);

    @Override
    public boolean save(Tran tran, String customerName) {

        /*

        交易添加业务：
            在做添加之前,参数tran对象里少了customerId
            先处理客户相关的需求

            (1)判断customerName,根据客户名称在客户表里进行精确查询
                如果有这个客户,则取出这个客户的id,封装到tran对象中
                否则,创建一条客户信息,并取出该条客户信息的id,封装到tran对象中

            (2)经过（1）后，tran的信息就补全了,执行添加交易的操作

            (3)创建一条交易历史记录


         */

        boolean flag = true;

        Customer customer = customerDao.getCustomerByName(customerName);

        //如果customer为空,创建客户
        if(customer==null){

            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setName(customerName);
            customer.setCreateBy(tran.getCreateBy());
            customer.setCreateTime(DateTimeUtil.getSysTime());
            customer.setContactSummary(tran.getContactSummary());
            customer.setNextContactTime(tran.getNextContactTime());
            customer.setOwner(tran.getOwner());

            int count1 = customerDao.save(customer);
            if(count1!=1){
                flag = false;
            }
        }

        //通过以上操作,必定能拿到customerId
        tran.setCustomerId(customer.getId());

        //添加交易
        int count2 = tranDao.save(tran);
        if(count2!=1){
            flag = false;
        }

        //添加交易历史
        TranHistory tranHistory = new TranHistory();
        tranHistory.setId(UUIDUtil.getUUID());
        tranHistory.setTranId(tran.getId());
        tranHistory.setStage(tran.getStage());
        tranHistory.setMoney(tran.getMoney());
        tranHistory.setExpectedDate(tran.getExpectedDate());
        tranHistory.setCreateTime(DateTimeUtil.getSysTime());
        tranHistory.setCreateBy(tran.getCreateBy());

        int count3 = tranHistoryDao.save(tranHistory);
        if(count3!=1){
            flag = false;
        }

        return flag;
    }

    @Override
    public Tran detail(String id) {

        Tran tran = tranDao.detail(id);

        return tran;
    }

    @Override
    public List<TranHistory> getHistoryListByTranId(String tranId) {

        List<TranHistory> tranHistoryList = tranHistoryDao.getHistoryListByTranId(tranId);

        return tranHistoryList;

    }

    @Override
    public boolean changeStage(Tran tran) {

        boolean flag = true;

        //修改交易
        int count1 = tranDao.changeStage(tran);

        if (count1!=1){
            flag = false;
        }

        //生成交易历史
        TranHistory tranHistory = new TranHistory();
        tranHistory.setId(UUIDUtil.getUUID());
        tranHistory.setStage(tran.getStage());
        tranHistory.setCreateBy(tran.getEditBy());
        tranHistory.setCreateTime(DateTimeUtil.getSysTime());
        tranHistory.setExpectedDate(tran.getExpectedDate());
        tranHistory.setMoney(tran.getMoney());
        tranHistory.setTranId(tran.getId());
        tranHistory.setPossibility(tran.getPossibility());

        int count2 = tranHistoryDao.save(tranHistory);

        if (count2!=1){
            flag = false;
        }


        return flag;
    }

    @Override
    public Map<String, Object> getCharts() {

        //取得total
        int total = tranDao.getTotal();

        //取得dataList
        List<Map<String,Object>> dataList = tranDao.getCharts();

        //将total和dataList保存到map中
        Map<String,Object> map = new HashMap<>();
        map.put("total",total);
        map.put("dataList",dataList);

        return map;
    }
}
