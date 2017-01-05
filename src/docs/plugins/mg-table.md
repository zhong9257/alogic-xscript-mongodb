mg-table
=========

mg-table用于定位到一个mongodb的collection，并定义一个namespace。

mg-table可以定义在一个[mg-db](mg-db.md)环境下；如果没有定义在[mg-db](mg-db.md)环境下，那么就需要配置cli参数、db参数、table参数，mg-table会自动根据该参数使用一个集合。

mg-table并不是必须的，可以采用[mg-cli](mg-cli.md)或[mg-db](mg-db.md)替代。  

> 本指令对应mongodb的操作指令[MongoCollection](http://mongodb.github.io/mongo-java-driver/3.4/driver/tutorials/databases-collections/)  


### 实现类

com.alogic.xscript.mongodb.MgTable


### 配置参数

支持下列参数：

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | pid | mongodb的连接Id，如果放在kv-cli的环境下，则无需配置 |
| 2 | cid | 上下文对象的id,缺省为$$mg-db |
| 3 | cli | mongodb的连接Id，通常定义在mongo-pool.xml文件中 |
| 4 | db | 指定mongodb的数据库 |
| 5 | table | 指定mongodb的collection |


### 案例

下面是一个定位table的案例.

```xml
    <script>
        <using xmlTag = "mg-table" module="com.alogic.xscript.mongodb.MgTable"/>
        
    	<mg-table cli="globalMongoDBClientPool" db="mydb" table="zytest" >
    	    <!--下面是针对globalMongoDBClientPool连接的mydb数据库的文档zytest的操作-->
    		<mg-insert tagValue="insert1" many="true" doc="[{ &quot;a&quot;: &quot;aa&quot;, &quot;b&quot;: &quot;bb&quot;},{ &quot;a&quot;: &quot;ac&quot;, &quot;b&quot;: &quot;bc&quot;}]">
    				<!-- 子指令里取内容 -->
    			<doc></doc>
    		</mg-insert>
    	</mg-table>
    </script>
```