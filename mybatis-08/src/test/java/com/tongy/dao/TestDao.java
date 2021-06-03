package com.tongy.dao;

import com.tongy.pojo.Blog;
import com.tongy.utils.IDUtil;
import com.tongy.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.*;

public class TestDao {

    @Test
    public void testAddBlog() throws Exception {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        BlogMapper mapper = sqlSession.getMapper(BlogMapper.class);
        Blog blog = new Blog();
        blog.setId(IDUtil.genId());
        blog.setTitle("java 编程");
        blog.setAuthor("Steffi");
        blog.setCreateTime(new Date());
        blog.setViews(9999);
        mapper.addBlog(blog);

        blog.setId(IDUtil.genId());
        blog.setTitle("Spring如此简单");
        mapper.addBlog(blog);

        blog.setId(IDUtil.genId());
        blog.setTitle("微服务如此简单");
        mapper.addBlog(blog);

        sqlSession.close();

    }

    @Test
    public void testQueryBlogIf() throws Exception {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        BlogMapper mapper = sqlSession.getMapper(BlogMapper.class);
        Map<String, String> map = new HashMap<String, String>();
        //map.put("title", "java 编程");
        map.put("author", "Steffi");
        List<Blog> blogs = mapper.queryBlogIf(map);
        for (Blog blog: blogs) {
            System.out.println(blog);
        }
        sqlSession.close();
    }

    @Test
    public void testQueryBlogChoose() throws Exception {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        BlogMapper mapper = sqlSession.getMapper(BlogMapper.class);
        Map<String, String> map = new HashMap<String, String>();
//        map.put("title", "java 编程");
//        map.put("author", "Steffi");
        map.put("views", "9999");
        List<Blog> blogs = mapper.queryBlogChoose(map);
        for (Blog blog: blogs) {
            System.out.println(blog);
        }
        sqlSession.close();
    }

    @Test
    public void testQueryBlogForeach() throws Exception {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        BlogMapper mapper = sqlSession.getMapper(BlogMapper.class);
        List<String> titles = new ArrayList<String>();
        titles.add("java 编程");
        titles.add("Spring如此简单");
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        map.put("titles", titles);
        List<Blog> blogs = mapper.queryBlogForeach(map);
        for (Blog blog: blogs) {
            System.out.println(blog);
        }
        sqlSession.close();
    }
}
