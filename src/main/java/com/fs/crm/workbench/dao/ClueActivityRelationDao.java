package com.fs.crm.workbench.dao;

import com.fs.crm.workbench.domain.ClueActivityRelation;

import java.util.List;

public interface ClueActivityRelationDao {


    int unbind(String id);

    int save(ClueActivityRelation clueActivityRelation);

    List<ClueActivityRelation> getListByClueId(String clueId);

    int delete(ClueActivityRelation clueActivityRelation);
}
