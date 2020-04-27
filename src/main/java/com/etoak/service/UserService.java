package com.etoak.service;

import com.etoak.bean.User;

public interface UserService {
    /**
     * 添加用户
     * @param user
     * @return
     */
    int addUser(User user);

    /**
     * 根据用户名查询用户
     * @param name
     * @return
     */
    User queryByName(String name);
}
