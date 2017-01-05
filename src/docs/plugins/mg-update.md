mg-update.md
=======

mg-update.根据子标签<filter/>组合出的更新条件更新集合中的一个或多个document，更新结果返回到当前文档tag的值指定的节点.  

> 本指令对应mongodb的操作指令[collection.updateOne和collection.updateMany概述](http://mongodb.github.io/mongo-java-driver/3.4/driver/tutorials/perform-write-operations/)和[collection.updateOne和collection.updateMany API](http://mongodb.github.io/mongo-java-driver/3.4/javadoc/?com/mongodb/client/MongoCollection.html#updateOne-org.bson.conversions.Bson-)  

### 实现类

com.alogic.xscript.mongodb.MgUpdate

### 配置参数

支持下列参数：

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | pid | mongodb的集合id，缺省为$mg-table |
| 2 | tag | 当前文档的节点,支持计算,缺省为$mg-update |
| 3 | doc | 更新内容,符合json格式的字符串,支持计算，缺省为""|
| 4 | docNode | 更新内容来自docNode指定的子标签包含的xscript2.0插件执行结果集中docResultNode指定的节点的值，docNode缺省为"doc"|
| 5 | docResultNode |  更新内容来自docNode指定的子标签包含的xscript2.0插件执行结果集中docResultNode指定的节点的值，docResultNode缺省为"doc"|
| 6 | many | true,表示更新多个文档；false表示更新单个文档；缺省为false |

>doc和docNode两种方式二选一；当doc存在且不为空，docNode也存在时；待插入文档内容为doc生效。

### 案例

现有下列脚本

```xml
<script>
	<using xmlTag = "mg-table" module="com.alogic.xscript.mongodb.MgTable"/>
		
	<mg-table cli="globalMongoDBClientPool" db="demo" table="test" >
		<mg-update many="true" doc="{$inc:{num:100}}">		
			<filter module="And">
				<filter module="Eq" field="a" value="aa-cli" ></filter>
				<filter module="Eq" field="num" value="305" type="integer"></filter>
			</filter>
		</mg-update>
	</mg-table>
	
</script>
```

结果

```json
	{"$mg-update":{"matchedCount":0,"upsertedId":"","modifiedCount":0}}
```
