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

## 读写分离

### MySQL主从复制环境搭建

1. Docker安装两个MySQL实例
```bash
docker run -p 3306:3306 --name mysql_01 -e MYSQL_ROOT_PASSWORD=abc123 -d mysql:5.7
docker run -p 3307:3306 --name mysql_02 -e MYSQL_ROOT_PASSWORD=abc123 -d mysql:5.7
```
2. 进入主库容器修改/etc/mysql/my.cnf配置文件，重启容器
```editorconfig
[mysqld]
## 同一局域网内注意要唯一
server-id=100  
## 开启二进制日志功能，可以随便取（关键）
log-bin=mysql-bin
```

3. 进入从库容器修改/etc/mysql/my.cnf配置文件，重启容器
```editorconfig
[mysqld]
## 设置server_id,注意要唯一
server-id=101  
## 开启二进制日志功能，以备Slave作为其它Slave的Master时使用
log-bin=mysql-slave-bin   
## relay_log配置中继日志
relay_log=edu-mysql-relay-bin 
```

4. 查看主节点状态信息，在主节点创建主从同步账号

```bash
mysql> show master status;
+------------------+----------+--------------+------------------+-------------------+
| File             | Position | Binlog_Do_DB | Binlog_Ignore_DB | Executed_Gtid_Set |
+------------------+----------+--------------+------------------+-------------------+
| mysql-bin.000001 |      617 |              |                  |                   |
+------------------+----------+--------------+------------------+-------------------+
1 row in set (0.00 sec)
mysql> CREATE USER 'slave'@'%' IDENTIFIED BY 'abc123';
Query OK, 0 rows affected (0.03 sec)

mysql> GRANT REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'slave'@'%';
Query OK, 0 rows affected (0.08 sec)
```

5. 在从节点执行命令，其中master_host可在物理机使用docker命令`docker inspect --format='{{.NetworkSettings.IPAddress}}' 容器名称|容器id`查看，其余信息和上面主节点状态信息对应

```bash
mysql> change master to master_host='172.17.0.2', master_user='slave', master_password='abc123', master_port=3306, master_log_file='mysql-bin.000001', master_log_pos= 617, master_connect_retry=30;
Query OK, 0 rows affected, 2 warnings (0.77 sec)
```

6. 在从节点开启主从复制，查看主从复制状态，`Slave_IO_Running: Yes Slave_SQL_Running: Yes`表示同步成功
                                   
```bash
mysql> start slave;
Query OK, 0 rows affected (0.03 sec)
mysql> show slave status \G;
*************************** 1. row ***************************
               Slave_IO_State: Waiting for master to send event
                  Master_Host: 172.17.0.2
                  Master_User: slave
                  Master_Port: 3306
                Connect_Retry: 30
              Master_Log_File: mysql-bin.000001
          Read_Master_Log_Pos: 617
               Relay_Log_File: edu-mysql-relay-bin.000002
                Relay_Log_Pos: 320
        Relay_Master_Log_File: mysql-bin.000001
             Slave_IO_Running: Yes
            Slave_SQL_Running: Yes
              Replicate_Do_DB:
          Replicate_Ignore_DB:
           Replicate_Do_Table:
       Replicate_Ignore_Table:
      Replicate_Wild_Do_Table:
  Replicate_Wild_Ignore_Table:
                   Last_Errno: 0
                   Last_Error:
                 Skip_Counter: 0
          Exec_Master_Log_Pos: 617
              Relay_Log_Space: 531
              Until_Condition: None
               Until_Log_File:
                Until_Log_Pos: 0
           Master_SSL_Allowed: No
           Master_SSL_CA_File:
           Master_SSL_CA_Path:
              Master_SSL_Cert:
            Master_SSL_Cipher:
               Master_SSL_Key:
        Seconds_Behind_Master: 0
Master_SSL_Verify_Server_Cert: No
                Last_IO_Errno: 0
                Last_IO_Error:
               Last_SQL_Errno: 0
               Last_SQL_Error:
  Replicate_Ignore_Server_Ids:
             Master_Server_Id: 100
                  Master_UUID: 9d2e26d9-4a77-11ec-a7f9-0242ac110002
             Master_Info_File: /var/lib/mysql/master.info
                    SQL_Delay: 0
          SQL_Remaining_Delay: NULL
      Slave_SQL_Running_State: Slave has read all relay log; waiting for more updates
           Master_Retry_Count: 86400
                  Master_Bind:
      Last_IO_Error_Timestamp:
     Last_SQL_Error_Timestamp:
               Master_SSL_Crl:
           Master_SSL_Crlpath:
           Retrieved_Gtid_Set:
            Executed_Gtid_Set:
                Auto_Position: 0
         Replicate_Rewrite_DB:
                 Channel_Name:
           Master_TLS_Version:
1 row in set (0.00 sec)
```

### Sharing-JDBC实现读写分离 架构图

![](https://my-guliedu.oss-cn-beijing.aliyuncs.com/sharding-jdbc/%E4%B8%BB%E4%BB%8E%E5%A4%8D%E5%88%B6.png)

# Sharing-Proxy

![](https://shardingsphere.apache.org/document/current/img/shardingsphere-proxy_v2.png)

## 参考文档

* [ShardingSphere官网](https://shardingsphere.apache.org/index_zh.html)
* [尚硅谷-SharingSphere](https://www.bilibili.com/video/BV1LK411s7RX)
* [狂神-SharingSphere](https://www.kuangstudy.com/zl/sharding)