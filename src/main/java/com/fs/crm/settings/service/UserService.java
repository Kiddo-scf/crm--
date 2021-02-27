package com.fs.crm.settings.service;

import com.fs.crm.exception.LoginException;
import com.fs.crm.settings.domain.User;

import java.util.List;


public interface UserService {
    User login(String loginAct, String loginPwd, String ip) throws LoginException;

    List<User> getUserList();
}
