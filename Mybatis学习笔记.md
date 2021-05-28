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

