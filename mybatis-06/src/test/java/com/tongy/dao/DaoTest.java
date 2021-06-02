package com.tongy.dao;

import com.tongy.pojo.Student;
import com.tongy.pojo.Teacher;
import com.tongy.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

public class DaoTest {

    @Test
    public void test() throws Exception {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        TeacherMapper mapper = sqlSession.getMapper(TeacherMapper.class);
        Teacher teacher = mapper.getTeacher2(1);
        System.out.println(teacher);

        sqlSession.close();
    }
}
