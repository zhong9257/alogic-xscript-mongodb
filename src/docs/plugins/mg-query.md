mg-query
======

mg-query：文档查询，用于查询指定数据库集合中的文档。

> 本指令对应mongodb的操作指令[collection.find]()

### 实现类

com.alogic.xscript.mongodb.MgQuery

### 配置参数

支持下列参数：

| 编号 | 代码 | 参数 | 说明  |
| ---- | ---- | ---- | ---- |
| 1 | db | 自定义 |连接的数据库名，位于mg-db标签|
| 2 | table | 自定义 |操作的集合名，位于mg-table标签|
| 3 | tagValue | 自定义 |返回的标志|
| 4 | first | true,false |是否只返回一个文档|
| 5 | sort | 自定义 |对返回的文档进行排序，用分号";"将正排序的字段和倒排序的字段隔开，分号必须存在，多个字段间用逗号","隔开。若只进行正排序，分号前面不写，但分号必须有|
| 6 | limit | 整数 |指定返回文档的数量|
| 7 | offset | 整数 |跳过文档的数量|
| 8 | projection | 自定义 |多个字段用逗号","隔开|
| 9 | filter | 子标签 |用于对集合中的文档进行过滤|

### 案例

输出结果：
```

```
#### 实现

具体实现代码如下：
```
<?xml version="1.0"?>
<script>
	<using xmlTag = "mg-cli" module="com.alogic.xscript.mongodb.MgClient"/>
	<mg-cli cli="globalMongoDBClientPool">
		<mg-db db="test">
			<mg-table table="stores" >
				<mg-query tagValue="data" first="false" sort=";name,_id" limit="2" offset="1"
				 projection="name,description">
					<filter module="Eq" field="name" value="Burger Buns"></filter>
				</mg-query>
			</mg-table>
		</mg-db>
	</mg-cli>
</script> 

```