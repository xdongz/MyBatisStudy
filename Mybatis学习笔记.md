#   MyBatis学习笔记



 MyBatis官网：https://mybatis.org/mybatis-3/zh/index.html

# 1.简介

* MyBatis 是一款优秀的**持久层**框架

* 它支持自定义 SQL、存储过程以及高级映射

* MyBatis 免除了几乎所有的 JDBC 代码以及设置参数和获取结果集的工作。MyBatis 可以通过简单的 XML 或注解来配置和映射原始类型、接口和 Java POJO（Plain Old Java Objects，普通老式 Java 对象）为数据库中的记录



# 2. 第一个MyBatis程序

* 第一步：构建SqlSessionFactory:

  ```java
  public class MybatisUtils {
      private static SqlSessionFactory sqlSessionFactory;
      static {
          try {
              // 使用Mybatis第一步： 获取sqlSessionFactory对象
              String resource = "mybatis-config.xml";
              InputStream inputStream = Resources.getResourceAsStream(resource);
              sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
          } catch (IOException e) {
              e.printStackTrace();
          }
      }
  
      // 既然有了SqlSessionFactory，顾名思义，我们就可以从中获得SqlSession的实例了。
      // SqlSession 完全包含了面向数据库执行SQL命令所需的所有方法
      public static SqlSession getSqlSession() {
          return sqlSessionFactory.openSession();
      }
  }
  ```



​       在构建SqlSessionFactory的时候需要用到一个xml文件：mybatis-config.xml.  ==注意：这个xml文件中不能有中文注释，否则运行会报错==

​       XML 配置文件中包含了对 MyBatis 系统的核心设置，包括获取数据库连接实例的数据源（DataSource）以及决定事务作用域和控制方式的事务管理器 （TransactionManager）

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
  PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-config.dtd">

<configuration>
  <environments default="development">
    <environment id="development">
      <transactionManager type="JDBC"/>
      <dataSource type="POOLED">
        <property name="driver" value="com.mysql.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://localhost:3306/mybatis?useSSL=false&amp;useUnicode=true&amp;characterEncoding=UTF-8"/>
        <property name="username" value="root"/>
        <property name="password" value="1234"/>
      </dataSource>
    </environment>
  </environments>
  <!--这一句很重要，表示映射的是哪个实现类，这样通过getMapper得到的就是这个类的实例-->
  <mappers>
    <mapper resource="com/tongy/dao/UserMapper.xml"/>
  </mappers>
</configuration>
```

​      

* 第二步：创建实体类

```java
public class User {
    private int id;
    private String name;
    private String pwd;
    
    // 构造，有参，无参
    // get/set
    // toString()
}
```



* 第三步：编写Mapper接口类

  ```java
  public interface UserDao {
      List<User> getUserList();
  }
  ```

* 第四步：编写Mapper.xml配置文件（相当于是Mapper接口类的实现）

  ```xml
  <?xml version="1.0" encoding="UTF-8" ?>
  <!DOCTYPE mapper
          PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
          "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
  <!--namespace用来绑定接口-->
  <mapper namespace="com.tongy.dao.UserDao">
      <!--id是接口的方法名称 resultType是返回类型-->
      <select id="getUserList" resultType="com.tongy.pojo.User">
          select * from mybatis.user
      </select>
  </mapper>
  ```

* 第五步：编写测试类

  ```java
  public class UserDaoTest {
  
      @Test
      public void test() {
          // 第一步：获得SqlSession 对象
          SqlSession sqlSession = MybatisUtils.getSqlSession();
          // 第二步：执行SQL
          // getMapper后面的参数是接口的类
          UserDao userDao = sqlSession.getMapper(UserDao.class);
          List<User> userList = userDao.getUserList();
          for (User user : userList) {
              System.out.println(user);
          }
  
          // 关闭session
          sqlSession.close();
      }
  }
  ```



==问题说明== ：Maven静态资源过滤问题

出现ExceptionInInitializerError：获取UserMapper.xml时找不到该文件

在pom.xml中加入：

```xml
<build>
  <resources>
    <resource>
      <directory>src/main/resources</directory>
      <includes>
        <include>**/*.properties</include>
        <include>**/*.xml</include>
      </includes>
      <filtering>true</filtering>
    </resource>
    <resource>
      <directory>src/main/java</directory>
      <includes>
        <include>**/*.properties</include>
        <include>**/*.xml</include>
      </includes>
      <filtering>true</filtering>
    </resource>
  </resources>
</build>
```

