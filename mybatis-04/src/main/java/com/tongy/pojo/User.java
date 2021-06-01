package com.tongy.pojo;

import org.apache.ibatis.type.Alias;

// 实体类
public class User {
    private int id;
    private String name;
    // 密码的变量名和数据库不一致，如果用mybatis-03的方法就会使得password为null
    private String password;

    public User() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", pwd='" + password + '\'' +
                '}';
    }
}
