mg-cli
=========

mg-cli用于定位到一个Mongod或者是集群Mongos的连接client，并定义一个namespace。

mg-cli并不是必须的，可以采用[mg-db](mg-db.md)或[mg-table](mg-table)替代。

### 实现类

com.alogic.xscript.mongodb.MgClient


### 配置参数

支持下列参数：

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | cli | mongodb的连接Id，通常定义在mongo-pool.xml文件中 |
| 2 | cid | 上下文对象的id,缺省为$mg-cli |


### 案例

下面是一个定位schema的案例.

```
	
	<script>
    	<using xmlTag = "mg-cli" module="com.alogic.xscript.mongodb.MgClient"/>
    	
    	<mg-cli cli="globalMongoDBClientPool">
    		<!--下面是针对globalMongoDBClientPool连接的操作-->
    	</mg-cli>
    </script>
	
```