package com.fs.crm.settings.service;

import com.fs.crm.settings.domain.DicType;
import com.fs.crm.settings.domain.DicValue;

import java.util.List;
import java.util.Map;

public interface DicService {
    Map<String, List<DicValue>> getAll();
}
