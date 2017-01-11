mg-replaceone
======

mg-replaceone：替换文档，按条件查询出文档，用指定的文档替换原文档。

> 本指令对应mongodb的操作指令[collection.replaceOne](http://mongodb.github.io/mongo-java-driver/3.4/javadoc/?com/mongodb/client/MongoCollection.html#find--)

### 实现类

com.alogic.xscript.mongodb.MgReplaceOne

### 配置参数

支持下列参数：

| 编号 | 代码 | 参数 | 说明  |
| ---- | ---- | ---- | ---- |
| 1 | doc | json格式的字符串  |待插入的文档,符合json格式的字符串,支持计算，缺省为""|
| 2 | docNode | |待插入的文档内容来自docNode指定的子标签包含的xscript2.0插件执行结果集中docResultNode指定的节点的值，docNode缺省为"doc"|
| 3 | docResultNode | | 待插入的文档内容来自docNode指定的子标签包含的xscript2.0插件执行结果集中docResultNode指定的节点的值，docResultNode缺省为"doc"|
| 4 | idKey | 自定义   |指定待插入文档的ObjectId,缺省为_id |
| 5 | db | 自定义 |连接的数据库名，位于mg-db标签|
| 6 | table | 自定义 |操作的集合名，位于mg-table标签|
| 7 | tag | 自定义 |返回的标志，默认返回"$mg-queryoneanddelete"|
| 8 | upsert | 布尔值 |是否在没有匹配到的时候插入替换的文档|
| 9 | bypassDocumentValidation | 布尔值 |是否启用文档验证|
| 10 | filter | 子标签 |用于对集合中的文档进行过滤，详细参考filter.md|

### 案例
实验在数据库test，集合demo上进行测试，测试数据如下：

| _id | a | b |
| ---- | ---- | ---- |
| ObjectId("5874aaf3e2012bdafb346e10") | 1.0 | 1.0 |
| ObjectId("5874aaf7e2012bdafb346e11") | 1.0 | 2.0 |
| ObjectId("5874aaf9e2012bdafb346e12") | 1.0 | 3.0 |
| ObjectId("5875f21bbccf39a53ba67311") | 1.0 | 4.0 |

> 查询条件：查询出a=1.0，b=2.0的文档，将其替换为{"replace":1101111}。

输出结果：
```
{
    "$mg-replaceone": { }
}
```
#### 实现

具体实现代码如下：
```
<?xml version="1.0"?>
<script>
	<using xmlTag = "mg-table" module="com.alogic.xscript.mongodb.MgTable"/>
	<mg-table cli="globalMongoDBClientPool" db="test" table="demo" >
		<mg-replaceone upsert="true" doc="{&quot;replace&quot;:&quot;1101111&quot;}">		
			<filter module="And">
				<filter module="Eq" field="a" value="1.0" type="double"></filter>
				<filter module="Eq" field="b" value="2.0" type="double"></filter>
			</filter>
		</mg-replaceone>
	</mg-table>	
</script> 

```