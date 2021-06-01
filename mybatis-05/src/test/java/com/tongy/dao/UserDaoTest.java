package com.tongy.dao;

import com.tongy.pojo.User;
import com.tongy.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

public class UserDaoTest {

    @Test
    public void testGetAllUsers() {
        SqlSession session = MybatisUtils.getSqlSession();
        //本质上利用了jvm的动态代理机制
        UserDao mapper = session.getMapper(UserDao.class);

        List<User> users = mapper.getAllUser();
        for (User user : users){
            System.out.println(user);
        }

        session.close();
    }

    @Test
    public void testGetUser() {
        SqlSession session = MybatisUtils.getSqlSession();
        //本质上利用了jvm的动态代理机制
        UserDao mapper = session.getMapper(UserDao.class);

        User user = mapper.getUserById(1);
        System.out.println(user);

        session.close();
    }

    @Test
    public void testAddUser() {
        SqlSession session = MybatisUtils.getSqlSession();
        //本质上利用了jvm的动态代理机制
        UserDao mapper = session.getMapper(UserDao.class);

        mapper.addUser(new User(6,"zzzz", "22222"));
        session.commit();
        session.close();
    }
}
