package com.tongy.dao;

import com.tongy.pojo.User;

import java.util.List;
import java.util.Map;

public interface UserDao {

    List<User> getUserList();

    User getUserByIde(int id);

    int update(User user);

    int insert(User user);

    int delete(int id);
}
