package com.fs.crm.workbench.service.impl;

import com.fs.crm.utils.SqlSessionUtil;
import com.fs.crm.workbench.dao.CustomerDao;
import com.fs.crm.workbench.service.CustomerService;

import java.util.List;

public class CustomerServiceImpl implements CustomerService {

    CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);

    @Override
    public List<String> getCustomerName(String name) {

        List<String> stringList = customerDao.getCustomerName(name);

        return stringList;
    }
}
