package com.tongy.dao;

import com.tongy.pojo.User;

import java.util.List;
import java.util.Map;

public interface UserDao {

    List<User> getUserLike(String value);

    List<User> getUserList();

    int update(User user);

    void update2(Map<String, Object> map);

    int insert(User user);

    int delete(int id);
}
