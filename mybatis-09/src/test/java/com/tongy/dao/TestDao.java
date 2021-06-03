package com.tongy.dao;

import com.tongy.pojo.User;
import com.tongy.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

public class TestDao {

    @Test
    public void test() throws Exception {
        SqlSession session = MybatisUtils.getSession();
        SqlSession session2 = MybatisUtils.getSession();
        UserMapper mapper = session.getMapper(UserMapper.class);
        UserMapper mapper2 = session2.getMapper(UserMapper.class);
        User user1 = mapper.getUserById(1);
        System.out.println(user1);
        session.close();

        // 要等第一个session 关闭后再创建第二个user对象，这样它才会从二级缓存中拿
        System.out.println("===============");
        User user2 = mapper2.getUserById(1);
        System.out.println(user1 == user2);

        session2.close();

    }
}
