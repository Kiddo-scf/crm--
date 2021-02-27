package com.fs.crm.workbench.service;

import com.fs.crm.workbench.domain.Activity;
import com.fs.crm.workbench.domain.Clue;
import com.fs.crm.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

public interface ClueService {
    boolean save(Clue clue);

    Clue getClueById(String id);

    boolean unbind(String id);

    boolean bind(String cid, String[] aids);

    boolean convert(String clueId, Tran tran, String createBy);
}
