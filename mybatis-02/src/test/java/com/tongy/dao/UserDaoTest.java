package com.tongy.dao;

import com.tongy.pojo.User;
import com.tongy.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

public class UserDaoTest {

    @Test
    public void test() {
        // 第一步：获得SqlSession 对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        // 第二步：执行SQL
        // 方式一：getMapper
        UserDao userDao = sqlSession.getMapper(UserDao.class);
        List<User> userList = userDao.getUserList();
        for (User user : userList) {
            System.out.println(user);
        }

        // 关闭session
        sqlSession.close();


    }
}
