package com.fs.crm.settings.service.impl;

import com.fs.crm.settings.dao.DicTypeDao;
import com.fs.crm.settings.dao.DicValueDao;
import com.fs.crm.settings.domain.DicType;
import com.fs.crm.settings.domain.DicValue;
import com.fs.crm.settings.service.DicService;
import com.fs.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DicServiceImpl implements DicService {
    private DicTypeDao dicTypeDao = SqlSessionUtil.getSqlSession().getMapper(DicTypeDao.class);
    private DicValueDao dicValueDao = SqlSessionUtil.getSqlSession().getMapper(DicValueDao.class);

    @Override
    public Map<String, List<DicValue>> getAll() {

        Map<String,List<DicValue>> map = new HashMap<>();

        //取出字典类型列表
        List<DicType> dicTypeCodeList = dicTypeDao.getTypeList();

        //按字典类型遍历
        for (DicType dicType : dicTypeCodeList) {
            String code = dicType.getCode();
            //取出每种字典类型对应的字典值列表
            List<DicValue> dicValueList = dicValueDao.getDicValueListByCode(code);
            map.put(code+"List",dicValueList);
        }

        return map;

    }
}
