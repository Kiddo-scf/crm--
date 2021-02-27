package com.fs.crm.workbench.dao;

import com.fs.crm.workbench.domain.Clue;

public interface ClueDao {


    int save(Clue clue);

    Clue getClueById(String id);

    Clue getById(String clueId);

    int delete(String clueId);
}
