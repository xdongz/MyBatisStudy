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



# 3.配置解析

## 1. 核心配置文件

* mybatis-config.xml

* MyBatis 的配置文件包含了会深深影响 MyBatis 行为的设置和属性信息

  ```xml
  configuration（配置）
  
  - [properties（属性）](https://mybatis.org/mybatis-3/zh/configuration.html#properties)
  - [settings（设置）](https://mybatis.org/mybatis-3/zh/configuration.html#settings)
  - [typeAliases（类型别名）](https://mybatis.org/mybatis-3/zh/configuration.html#typeAliases)
  - [typeHandlers（类型处理器）](https://mybatis.org/mybatis-3/zh/configuration.html#typeHandlers)
  - [objectFactory（对象工厂）](https://mybatis.org/mybatis-3/zh/configuration.html#objectFactory)
  - [plugins（插件）](https://mybatis.org/mybatis-3/zh/configuration.html#plugins)
  - environments（环境配置）
    - environment（环境变量）
      - transactionManager（事务管理器）
      - dataSource（数据源）
  - [databaseIdProvider（数据库厂商标识）](https://mybatis.org/mybatis-3/zh/configuration.html#databaseIdProvider)
  - [mappers（映射器）](https://mybatis.org/mybatis-3/zh/configuration.html#mappers)
  ```

  

## 2. 环境配置（environment）

MyBatis 可以配置成适应多种环境

**不过要记住：尽管可以配置多个环境，但每个 SqlSessionFactory 实例只能选择一种环境。**

MyBatis默认的事务管理器就是JDBC，数据源类型：POOLED

## 3. 属性（properties）

我们可以通过properties属性来实现引用配置文件

这些属性可以在外部进行配置，并可以进行动态替换。你既可以在典型的 Java 属性文件中配置这些属性，也可以在 properties 元素的子元素中设置

编写一个配置文件

```xml
driver=com.mysql.jdbc.Driver
url=jdbc:mysql://localhost:3306/mybatis?useSSL=false&useUnicode=true&characterEncoding=UTF-8
username=root
password=1234
```

在核心配置文件中引入

```xml
<properties resource="org/mybatis/example/config.properties">
  <property name="username" value="dev_user"/>
  <property name="password" value="F2Fa3!33TYyg"/>
</properties>
```



<img src="C:\Users\GX\AppData\Roaming\Typora\typora-user-images\image-20210528213624948.png" alt="image-20210528213624948" style="zoom:150%;" />

* 可以直接引入外部文件
* 可以在其中增加一些属性配置
* 如果两个文件有同一个字段，优先使用外部配置文件的

## 4.类型别名（typeAliases）

* 类型别名是为Java类型设置一个短的名字

* 存在的意义仅在于用来减少类完全限定名的冗余

  ```xml
    <typeAliases>
      <typeAlias type="com.tongy.pojo.User" alias="User"/>
    </typeAliases>
  ```

  也可以指定一个包名，MyBatis会在包名下面搜索需要的Java Bean，比如

  扫描实体类的包，它的默认别名就为这个类的类名，首字母小写

  ```xml
    <typeAliases>
      <package name="com.tongy.pojo"/>
    </typeAliases>
  ```

  在实体类较少的时候，使用第一行中

  实体类比较多，建议第二种

  第一种可以DIY起名，第二种则不行，如果非要改，需要在类上加注解

  ```java
  @Alias("hello")
  ```



## 5. 设置

​       这是 MyBatis 中极为重要的调整设置，它们会改变 MyBatis 的  运行时行为。

## 6. 映射（mappers）

方式一：使用resource

```xml
<mappers>
  <mapper resource="com/tongy/dao/UserMapper.xml"/>
</mappers>
```

方式二：使用class

```xml
<mappers>
  <mapper class="com.tongy.dao.UserMapper"/>
</mappers>
```

注意点：

* 接口和它的Mapper配置文件必须同名
* 接口和它的Mapper配置文件必须在同一个包下

方式三：使用扫描包进行注入绑定

```xml
<mappers>
  <mapper class="com.tongy.dao.UserMapper"/>
</mappers>
```

注意点：

* 接口和它的Mapper配置文件必须同名
* 接口和它的Mapper配置文件必须在同一个包下

## 7. 生命周期和作用域

![image-20210528224055546](C:\Users\GX\AppData\Roaming\Typora\typora-user-images\image-20210528224055546.png)

不同作用域和生命周期类别是至关重要的，因为错误的使用会导致非常严重的**并发问题**。

**SqlSessionFactoryBuilder**

* 一旦创建了 SqlSessionFactory，就不再需要它了
* 局部变量

**SqlSessionFactory**

* 可以想象成数据库连接池
* 一旦被创建就应该在应用的运行期间一直存在，**没有任何理由丢弃它或重新创建另一个实例**
*  SqlSessionFactory 的最佳作用域是应用作用域

* 最简单的就是使用单例模式或者静态单例模式

**SqlSession**

* 连接到连接池的一个请求

* SqlSession 的实例不是线程安全的，因此是不能被共享的，所以它的最佳的作用域是请求或方法作用域
* 用完之后赶紧关闭，否则资源被占用

![image-20210528224800276](C:\Users\GX\AppData\Roaming\Typora\typora-user-images\image-20210528224800276.png)

# 5. 日志

## 1. 日志工厂

如果一个数据库操作，出现了异常，我们需要排错。日志就是最好的助手

在MyBatis中绝体使用哪一个日志实现，在设置中设定

**STDOUT_LOGGING标准日志输出**

在MyBatis核心配置文件中，配置我们的日志

```xml
<settings>
    <setting name="logImpl" value="STDOUT_LOGGING"/>
</settings>
```

## 2. Log4j

1.导入Log4j包

```xml
<!-- https://mvnrepository.com/artifact/log4j/log4j -->
<dependency>
    <groupId>log4j</groupId>
    <artifactId>log4j</artifactId>
    <version>1.2.17</version>
</dependency>

```

2.log4j.properties

```xml
#将等级为DEBUG的日志信息输出到console和file这两个目的地，console和file的定义在下面的代码
log4j.rootLogger=DEBUG,console,file

#控制台输出的相关设置
log4j.appender.console = org.apache.log4j.ConsoleAppender
log4j.appender.console.Target = System.out
log4j.appender.console.Threshold=DEBUG
log4j.appender.console.layout = org.apache.log4j.PatternLayout
log4j.appender.console.layout.ConversionPattern=【%c】-%m%n

#文件输出的相关设置
log4j.appender.file = org.apache.log4j.RollingFileAppender
log4j.appender.file.File=./log/kuang.log
log4j.appender.file.MaxFileSize=10mb
log4j.appender.file.Threshold=DEBUG
log4j.appender.file.layout=org.apache.log4j.PatternLayout
log4j.appender.file.layout.ConversionPattern=【%p】【%d{yy-MM-dd}】【%c】%m%n

#日志输出级别
log4j.logger.org.mybatis=DEBUG
log4j.logger.java.sql=DEBUG
log4j.logger.java.sql.Statement=DEBUG
log4j.logger.java.sql.ResultSet=DEBUG
log4j.logger.java.sql.PreparedStatement=DEBUG
```

3. 配置log4j为日志的实现

**简单使用**

1. Logger.getLogger() 生成日志对象，参数为当前类的class
2. 日志级别

# 6.分页

## 使用Limit分页

```xml
语法： SELECT * from user limit startIndex,pageSize;
```

使用MyBatis实现分页，核心SQL

1.接口

2.Mapper.xml

3.测试

# 7.使用注解开发

1. 注解在接口上实现

   ```java
   @Select("select * from user")
   List<User> getUsers();
   ```

2. 需要在核心配置文件中绑定接口
3. 测试

   本质：反射机制

​    底层：动态代理

   **关于@Param()注解**

* 基本类型的参数或者String类型，需要加上
* 引用类型不需要加
* 如果只有一个基本类型的话，可以忽略，但建议加上
* 我们在SQL中引用的就是我们这里的@Param()中设定的属性名

  

   **#{} ${} 区别**

# 8.Lombok

Lombok项目是一个java库，它会自动插入编辑器和构建工具中，Lombok提供了一组有用的注释，用来消除Java类中的大量样板代码。

使用步骤：

      1. 在IDEA中安装Lombok插件
      2. 在项目中导入Lombok的jar包
      3. 在实体类上加注解即可

# 9.多对一处理

   测试环境搭建：

```xml
1. 导入lombok
2. 新建实体类Teacher, Student
3. 建立Mapper接口
4. 建立Mapper.xml文件
5. 在核心配置文件中绑定注册我们的Mapper接口或者文件
6. 测试查询是否能够成功
```

**按照查询嵌套处理**

```xml
<!--SQL Mapper xml file -->
<!-- 
    思路：
        1.查询所有的学生信息
        2.根据查询出来的学生的tid，寻找对应的老师   子查询
-->
    <select id="getStudents" resultMap="StudentTeacher">
        select * from student
    </select>
    <resultMap id="StudentTeacher" type="com.tongy.pojo.Student">
        <!--property中填写的是Student实体类中定义的字段
            column中填写的是对应的数据库中的字段
         -->
        <id property="id" column="id"/>
        <result property="name" column="name"/>
        <!--teacher是一个对象，需要用javaType指定它的类型-->
        <association property="teacher" javaType="com.tongy.pojo.Teacher" column="tid" select="getTeacher"/>
    </resultMap>

    <select id="getTeacher" resultType="com.tongy.pojo.Teacher">
        select * from teacher where id=#{id}
    </select>
```

**按照结果嵌套处理**

```xml
    <select id="getStudents2" resultMap="StudentTeacher2">
        select s.id sid, s.name sname, t.name tname
        from student s, teacher t
        where s.tid=t.id;
    </select>

    <resultMap id="StudentTeacher2" type="com.tongy.pojo.Student">
        <result property="id" column="sid"/>
        <result property="name" column="sname"/>
        <association property="teacher" javaType="com.tongy.pojo.Teacher">
            <result property="name" column="tname"/>
        </association>
    </resultMap>
```

回顾MySQL嵌套查询

* 子查询
* 联表查询



# 10. 多对一处理

**按照查询嵌套处理**

```xml
<select id="getTeacher2" resultMap="TeacherStudent2">
        select * from teacher where id=#{tid}
    </select>

    <resultMap id="TeacherStudent2" type="com.tongy.pojo.Teacher">
        <!-- id和name相同，下面这两行可以省略-->
        <result property="id" column="id"/>
        <result property="name" column="name"/>
        <collection property="students" javaType="ArrayList" ofType="com.tongy.pojo.Student" select="getStudentByTeacherId" column="id"/>
    </resultMap>

    <!--getStudentByTeacherId 不需要在class中定义-->
    <select id="getStudentByTeacherId" resultType="com.tongy.pojo.Student">
        select * from student where tid=#{tid}
    </select>
```



**按照结果嵌套处理**

```xml
    <select id="getTeachers" resultType="com.tongy.pojo.Teacher">
        select * from teacher
    </select>
    
    <select id="getTeacher" resultMap="TeacherStudent">
        SELECT s.id sid, s.name sname, t.id tid,t.name tname
        FROM student s, teacher t
        WHERE s.tid=t.id and t.id=#{tid};
    </select>
    
    <resultMap id="TeacherStudent" type="com.tongy.pojo.Teacher">
        <result property="id" column="tid"/>
        <result property="name" column="tname"/>
        <collection property="students" ofType="com.tongy.pojo.Student">
            <result property="id" column="sid"/>
            <result property="name" column="sname"/>
            <result property="tid" column="tid"/>
        </collection>
    </resultMap>
```



**小结**

1. 关联 - association 【多对一】
2. 集合 - collection 【一对多】
3. javaType  &  ofType
   1. javaType 用来指定实体类中属性的类型
   2. ofType 用来指定映射到集合中的pojo类型，泛型中的约束类型

**注意点：**

1. 保证SQL的可读性，尽量保证通俗易懂
2. 注意一对多和多对一中，属性名和字段的问题
3. 如果问题不好排查错误，可以使用日志，建议使用log4j

# 11. 动态SQL

==什么是动态SQL: 动态SQL就是指根据根据不同的条件生成不同的SQL语句==

```xml
如果你之前用过 JSTL 或任何基于类 XML 语言的文本处理器，你对动态 SQL 元素可能会感觉似曾相识。在 MyBatis 之前的版本中，需要花时间了解大量的元素。借助功能强大的基于 OGNL 的表达式，MyBatis 3 替换了之前的大部分元素，大大精简了元素种类，现在要学习的元素种类比原来的一半还要少。

if
choose (when, otherwise)
trim (where, set)
foreach
```

## **搭建环境**

```sql
CREATE TABLE `blog`(
`id` VARCHAR(50) NOT NULL COMMENT 博客id,
`title` VARCHAR(100) NOT NULL COMMENT 博客标题,
`author` VARCHAR(30) NOT NULL COMMENT 博客作者,
`create_time` DATETIME NOT NULL COMMENT 创建时间,
`views` INT(30) NOT NULL COMMENT 浏览量
)ENGINE=INNODB DEFAULT CHARSET=utf8
```

创建一个基础工程

1. 导包
2. 编写配置文件
3. 编写实体类
4. 编写实体类对应的Mapper接口和Mapper.xml

## if

```xml
   <select id="queryBlogIf" resultType="com.tongy.pojo.Blog" parameterType="map">
        select * from blog
        <where>
            <if test="title!=null">
                title=#{title}
            </if>
            <if test="author!=null">
                and author=#{author}
            </if>
        </where>
    </select>
```



## choose (when,  otherwise)

```xml
  <select id="queryBlogChoose" resultType="com.tongy.pojo.Blog" parameterType="map">
        select * from blog
        <where>
            <choose>
                <when test="title!=null">
                    title=#{title}
                </when>
                <when test="author!=null">
                    author=#{author}
                </when>
                <otherwise>
                    views=#{views}
                </otherwise>
            </choose>
        </where>
    </select>
```



## trim (where, set)

## SQL片段

有时候我们可能会将一些功能的部分抽取出来，方便复用

1. 使用SQL标签抽取公共的部分

   ```xml
   <sql id="if-title-author">
      <if test="title != null">
         title = #{title}
      </if>
      <if test="author != null">
         and author = #{author}
      </if>
   </sql>
   ```

   

2. 在需要使用的地方使用include标签引用即可

   ```xml
   <select id="queryBlogIf" parameterType="map" resultType="blog">
     select * from blog
      <where>
          <!-- 引用 sql 片段，如果refid 指定的不在本文件中，那么需要在前面加上 namespace -->
          <include refid="if-title-author"></include>
          <!-- 在这里还可以引用其他的 sql 片段 -->
      </where>
   </select>
   ```

   

注意事项：

* 最好基于单表来定义SQL片段
* 不要存在where标签

## Foreach

1. 编写接口

   ```java
   List<Blog> queryBlogForeach(Map map);
   ```

   

2. 编写SQL语句

   ```xml
   <select id="queryBlogForeach" parameterType="map" resultType="blog">
     select * from blog
      <where>
          <!--
          collection:指定输入对象中的集合属性
          item:每次遍历生成的对象
          open:开始遍历时的拼接字符串
          close:结束时拼接的字符串
          separator:遍历对象之间需要拼接的字符串
        -->
          <foreach collection="titles"  item="title" open="and (" close=")" separator="or">
             title=#{title}
          </foreach>
      </where>
   </select>
   ```

   

3. 测试

   ```java
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
   ```





# 12. 缓存

## 12.1简介

1、什么是缓存 [ Cache ]？

- 存在内存中的临时数据。
- 将用户经常查询的数据放在缓存（内存）中，用户去查询数据就不用从磁盘上(关系型数据库数据文件)查询，从缓存中查询，从而提高查询效率，解决了高并发系统的性能问题。

2、为什么使用缓存？

- 减少和数据库的交互次数，减少系统开销，提高系统效率。

3、什么样的数据能使用缓存？

- 经常查询并且不经常改变的数据

## 12.2 Mybatis缓存

- MyBatis包含一个非常强大的查询缓存特性，它可以非常方便地定制和配置缓存。缓存可以极大的提升查询效率。

- MyBatis系统中默认定义了两级缓存：**一级缓存**和**二级缓存**

- - 默认情况下，只有一级缓存开启。（SqlSession级别的缓存，也称为本地缓存）
  - 二级缓存需要手动开启和配置，他是基于namespace级别的缓存。
  - 为了提高扩展性，MyBatis定义了缓存接口Cache。我们可以通过实现Cache接口来自定义二级缓存

## 12.3 一级缓存

一级缓存也叫本地缓存：

- 与数据库同一次会话期间查询到的数据会放在本地缓存中。
- 以后如果需要获取相同的数据，直接从缓存中拿，没必须再去查询数据库；

```java
    @Test
    public void test() throws Exception {
        SqlSession session = MybatisUtils.getSession();
        UserMapper mapper = session.getMapper(UserMapper.class);
        User user1 = mapper.getUserById(1);
        System.out.println(user1);

        System.out.println("===============");
        User user2 = mapper.getUserById(1);
        // 返回结果为true
        System.out.println(user1 == user2);

        session.close();
    }
```

**一级缓存失效的四种情况**

一级缓存是SqlSession级别的缓存，是一直开启的，我们关闭不了它；

一级缓存失效情况：没有使用到当前的一级缓存，效果就是，还需要再向数据库中发起一次查询请求！

1. sqlSession不同

```java
@Test
public void testQueryUserById(){
   SqlSession session = MybatisUtils.getSession();
   SqlSession session2 = MybatisUtils.getSession();
   UserMapper mapper = session.getMapper(UserMapper.class);
   UserMapper mapper2 = session2.getMapper(UserMapper.class);

   User user = mapper.queryUserById(1);
   System.out.println(user);
   User user2 = mapper2.queryUserById(1);
   System.out.println(user2);
    // false
   System.out.println(user==user2);

   session.close();
   session2.close();
}
```

观察结果：发现发送了两条SQL语句！

结论：**每个sqlSession中的缓存相互独立**

2. sqlSession相同，查询条件不同

```java
@Test
public void testQueryUserById(){
   SqlSession session = MybatisUtils.getSession();
   UserMapper mapper = session.getMapper(UserMapper.class);
   UserMapper mapper2 = session.getMapper(UserMapper.class);

   User user = mapper.queryUserById(1);
   System.out.println(user);
   User user2 = mapper2.queryUserById(2);
   System.out.println(user2);
    // false
   System.out.println(user==user2);

   session.close();
}
```

观察结果：发现发送了两条SQL语句！很正常的理解

结论：**当前缓存中，不存在这个数据**

3. sqlSession相同，两次查询之间执行了增删改操作！

```java
@Test
public void testQueryUserById(){
   SqlSession session = MybatisUtils.getSession();
   UserMapper mapper = session.getMapper(UserMapper.class);

   User user = mapper.queryUserById(1);
   System.out.println(user);

   HashMap map = new HashMap();
   map.put("name","kuangshen");
   map.put("id",4);
   mapper.updateUser(map);

   User user2 = mapper.queryUserById(1);
   System.out.println(user2);
   // false
   System.out.println(user==user2);

   session.close();
}
```

4. sqlSession相同，手动清除一级缓存

```java
@Test
public void testQueryUserById(){
   SqlSession session = MybatisUtils.getSession();
   UserMapper mapper = session.getMapper(UserMapper.class);

   User user = mapper.queryUserById(1);
   System.out.println(user);

   session.clearCache();//手动清除缓存

   User user2 = mapper.queryUserById(1);
   System.out.println(user2);
   // false
   System.out.println(user==user2);

   session.close();
}
```

## 12.4 二级缓存

- 二级缓存也叫全局缓存，一级缓存作用域太低了，所以诞生了二级缓存

- 基于namespace级别的缓存，一个名称空间，对应一个二级缓存；

- 工作机制

- - 一个会话查询一条数据，这个数据就会被放在当前会话的一级缓存中；
  - 如果当前会话关闭了，这个会话对应的一级缓存就没了；但是我们想要的是，会话关闭了，一级缓存中的数据被保存到二级缓存中；
  - 新的会话查询信息，就可以从二级缓存中获取内容；
  - 不同的mapper查出的数据会放在自己对应的缓存（map）中；

**使用步骤：**

1. 开启全局缓存 【mybatis-config.xml】

   ```xml
   <setting name="cacheEnabled" value="true"/>
   ```

2. 去每个mapper.xml中配置使用二级缓存，这个配置非常简单；【xxxMapper.xml】

   ```xml
   <cache/>
   
   官方示例=====>查看官方文档
   <cache
    eviction="FIFO"
    flushInterval="60000"
    size="512"
    readOnly="true"/>
   这个更高级的配置创建了一个 FIFO 缓存，每隔 60 秒刷新，最多可以存储结果对象或列表的 512 个引用，而且返回的对象被认为是只读的，因此对它们进行修改可能会在不同线程中的调用者产生冲突。
   ```

3. 代码测试

   - 所有的实体类先实现序列化接口
   - 测试代码

   ```java
   @Test
   public void testQueryUserById(){
      SqlSession session = MybatisUtils.getSession();
      SqlSession session2 = MybatisUtils.getSession();
   
      UserMapper mapper = session.getMapper(UserMapper.class);
      UserMapper mapper2 = session2.getMapper(UserMapper.class);
   
      User user = mapper.queryUserById(1);
      System.out.println(user);
      session.close();
   
      User user2 = mapper2.queryUserById(1);
      System.out.println(user2);
      System.out.println(user==user2);
   
      session2.close();
   }
   ```

   

**结论**

- 只要开启了二级缓存，我们在同一个Mapper中的查询，可以在二级缓存中拿到数据
- 查出的数据都会被默认先放在一级缓存中
- 只有会话提交或者关闭以后，一级缓存中的数据才会转到二级缓存中

## 12.5 缓存原理图

![image-20210603212243753](C:\Users\GX\AppData\Roaming\Typora\typora-user-images\image-20210603212243753.png)
