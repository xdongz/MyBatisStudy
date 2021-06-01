package com.tongy.dao;

import com.tongy.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserDao {

    User getUserById(int id);

    List<User> selectUser(@Param("startIndex") int startIndex,
                          @Param("pageSize") int pageSize);
}
