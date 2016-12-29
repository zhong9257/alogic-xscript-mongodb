mg-query
======

mg-query：文档查询，用于查询指定数据库集合中的文档。

> 本指令对应mongodb的操作指令[collection.find](http://mongodb.github.io/mongo-java-driver/3.4/driver/tutorials/perform-read-operations/)

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
实验在数据库test，集合stores上进行测试，测试数据如下：

| _id | name | description |
| ---- | ---- | ---- |
| 1.0 | Java Hut | Coffee and cakes |
| 2.0 | Burger Buns | Gourmet hamburgers |
| 3.0 | Coffee Shop | Just coffee |
| 4.0 | Clothes Clothes Clothes | Discount clothing |
| 5.0 | Java Shopping | Indonesian goods |
| 6 | Burger Buns |  |
| 8 | Burger Buns |  |

> 查询条件：查询全部文档，对name和_id字段进行逆排序，返回文档数量为2，跳过文档数量为1，只返回name字段

输出结果：
```
{
    "data": [
        {
            "_id": 6, 
            "name": "Burger Buns"
        }, 
        {
            "_id": 2, 
            "name": "Burger Buns"
        }
    ]
}
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
				<mg-query tag="data" first="false" sort=";name,_id" limit="2" offset="1"
				 projection="name">
					<filter module="Eq" field="name" value="Burger Buns"></filter>
				</mg-query>
			</mg-table>
		</mg-db>
	</mg-cli>
</script> 

```