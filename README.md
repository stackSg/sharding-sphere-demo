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

![](https://img.stacksg.club/sharding-jdbc/%E6%B0%B4%E5%B9%B3%E5%88%86%E5%BA%93%E5%88%86%E8%A1%A8.png)

## 参考文档

* [ShardingSphere官网](https://shardingsphere.apache.org/index_zh.html)
* [尚硅谷-SharingSphere](https://www.bilibili.com/video/BV1LK411s7RX)
* [狂神-SharingSphere](https://www.kuangstudy.com/zl/sharding)