package com.fs.crm.settings.dao;

import com.fs.crm.settings.domain.DicValue;

import java.util.List;

public interface DicValueDao {
    List<DicValue> getDicValueListByCode(String code);
}
