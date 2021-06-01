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
    public void testGetUserById() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserDao mapper = sqlSession.getMapper(UserDao.class);
        User userByIde = mapper.getUserById(1);
        System.out.println(userByIde);
        sqlSession.close();
    }

    @Test
    public void testSelectUser() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserDao mapper = sqlSession.getMapper(UserDao.class);

        int currentPage = 1;  //第几页
        int pageSize = 2;  //每页显示几个
//        Map<String,Integer> map = new HashMap<String,Integer>();
//        map.put("startIndex",(currentPage-1)*pageSize);
//        map.put("pageSize",pageSize);
        List<User> users = mapper.selectUser(0,2);
        for (User user : users) {
            System.out.println(user);
        }
        sqlSession.close();
    }
}
