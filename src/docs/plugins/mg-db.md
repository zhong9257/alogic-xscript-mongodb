mg-db
=========

mg-db用于定位到一个Mongod或者是集群Mongos的连接client，并定义一个namespace。  

mg-db可以定义在一个[mg-cli](mg-cli.md)环境下；如果没有定义在[mg-cli](mg-cli.md)环境下，那么就需要配置cli参数，mg-db会自动根据该参数使用一个cli。

mg-db并不是必须的，可以采用[mg-cli](mg-cli.md)或[mg-table](mg-table.md)替代。

### 实现类

com.alogic.xscript.mongodb.MgDB


### 配置参数

支持下列参数：

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | pid | mongodb的连接Id，如果放在kv-cli的环境下，则无需配置 |
| 2 | cid | 上下文对象的id,缺省为$$mg-db |
| 3 | cli | mongodb的连接Id，通常定义在mongo-pool.xml文件中 |
| 4 | db | 指定mongodb的数据库 |



### 案例

下面是一个定位db的案例.

```
	
	<script>
        <using xmlTag = "mg-db" module="com.alogic.xscript.mongodb.MgDB"/>

        <mg-db cli="globalMongoDBClientPool" db="mydb">
		<!--下面是针对globalMongoDBClientPool连接的mydb数据库的操作-->
	    </mg-db>
    </script>
	
```