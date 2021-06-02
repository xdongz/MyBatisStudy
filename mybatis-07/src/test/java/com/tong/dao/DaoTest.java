package com.tong.dao;

import com.tong.pojo.Student;
import com.tong.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

public class DaoTest {

    @Test
    public void test() throws Exception {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper mapper = sqlSession.getMapper(StudentMapper.class);
        List<Student> students = mapper.getStudents2();
        for (Student student: students) {
            System.out.println(student);
        }
        sqlSession.close();
    }
}
