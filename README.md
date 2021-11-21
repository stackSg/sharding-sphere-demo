# Sharing-JDBC学习

## 技术栈

* Spring Boot 2.2 + MyBatisPlus 3.0 + Sharing-JDBC 4.0 + Druid 1.2

## 水平分库分表

### 表结构

```sql
CREATE TABLE `course` (
  `cid` bigint(20) NOT NULL,
  `cname` varchar(255) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `cstatus` varchar(10) NOT NULL,
  PRIMARY KEY (`cid`)
)
```

### 架构图

![](https://my-guliedu.oss-cn-beijing.aliyuncs.com/sharding-jdbc/%E6%B0%B4%E5%B9%B3%E5%88%86%E5%BA%93%E5%88%86%E8%A1%A8.png)

## 垂直分库分表

### 专库专表

```sql
CREATE TABLE `t_user` (
  `user_id` bigint(20) NOT NULL,
  `username` varchar(255) NOT NULL,
  `ustatus` varchar(10) NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
```

### 架构图

![](https://my-guliedu.oss-cn-beijing.aliyuncs.com/sharding-jdbc/%E4%B8%93%E5%BA%93%E4%B8%93%E8%A1%A8.png)

## 公共表

* 存储固定数据的表，表数据很少发生变化，查询时经常进行关联
* 在每个数据库中创建出相同结构公共表
* 插入时类似广播模式 每个数据库中的该表都存储相同的数据

### 表结构

```sql
CREATE TABLE `t_udict` (
  `dictid` bigint(20) NOT NULL,
  `ustatus` varchar(100) NOT NULL,
  `uvalue` varchar(100) NOT NULL,
  PRIMARY KEY (`dictid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
```

### 架构图

![](https://my-guliedu.oss-cn-beijing.aliyuncs.com/sharding-jdbc/%E5%85%AC%E5%85%B1%E8%A1%A8.png)

## 参考文档

* [ShardingSphere官网](https://shardingsphere.apache.org/index_zh.html)
* [尚硅谷-SharingSphere](https://www.bilibili.com/video/BV1LK411s7RX)
* [狂神-SharingSphere](https://www.kuangstudy.com/zl/sharding)