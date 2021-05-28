package com.tongy.dao;

import com.tongy.pojo.User;
import com.tongy.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDaoTest {

    @Test
    public void testGetUserLike() {
        // 第一步：获得SqlSession 对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        // 第二步：执行SQL
        // 方式一：getMapper
        UserDao userDao = sqlSession.getMapper(UserDao.class);

        // 查询
        List<User> userList = userDao.getUserLike("%a%y%");
        for (User user : userList) {
            System.out.println(user);
        }
        // 关闭session
        sqlSession.close();
    }

    @Test
    public void test() {
        // 第一步：获得SqlSession 对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        // 第二步：执行SQL
        // 方式一：getMapper
        UserDao userDao = sqlSession.getMapper(UserDao.class);

        // 查询
        List<User> userList = userDao.getUserList();
        for (User user : userList) {
            System.out.println(user);
        }
        // 关闭session
        sqlSession.close();
    }

    @Test
    public void testUpdateUser() {
        // 第一步：获得SqlSession 对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        // 第二步：执行SQL
        // 方式一：getMapper
        UserDao userDao = sqlSession.getMapper(UserDao.class);

        userDao.update(new User(3, "happy", "5555"));
        // 一定要提交事务，才能显示变化
        sqlSession.commit();

        // 关闭session
        sqlSession.close();
    }

    @Test
    public void testUpdateUser2() {
        // 第一步：获得SqlSession 对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        // 第二步：执行SQL
        // 方式一：getMapper
        UserDao userDao = sqlSession.getMapper(UserDao.class);

        Map<String, Object> map = new HashMap();
        map.put("userid", 1);
        map.put("userpwd", "123123");
        userDao.update2(map);
        // 一定要提交事务，才能显示变化
        sqlSession.commit();

        // 关闭session
        sqlSession.close();
    }

    @Test
    public void testAddUser() {
        // 第一步：获得SqlSession 对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        // 第二步：执行SQL
        // 方式一：getMapper
        UserDao userDao = sqlSession.getMapper(UserDao.class);

        int happy = userDao.insert(new User(5, "friday", "5555"));
        sqlSession.commit();
        System.out.println(happy);

        // 关闭session
        sqlSession.close();
    }

    @Test
    public void testDeleteUser() {
        // 第一步：获得SqlSession 对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        // 第二步：执行SQL
        // 方式一：getMapper
        UserDao userDao = sqlSession.getMapper(UserDao.class);

        userDao.delete(4);
        sqlSession.commit();

        // 关闭session
        sqlSession.close();
    }
}
