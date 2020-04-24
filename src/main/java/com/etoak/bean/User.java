package com.etoak.bean;

import lombok.Data;

@Data
public class User {
    private Integer id;
    private String name;
    private String password;
    private String gender;
    private Integer age;
    private String birthday;
    // 用户邮箱
    private String email;
    // 用户状态
    private Integer state;
}
