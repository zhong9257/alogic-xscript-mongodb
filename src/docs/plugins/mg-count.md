mg-count
======

mg-count：统计，用于统计查询出来的文档数量。

> 本指令对应mongodb的操作指令[count](http://mongodb.github.io/mongo-java-driver/3.4/javadoc/?com/mongodb/client/MongoCollection.html#find--)

### 实现类

com.alogic.xscript.mongodb.MgCount

### 配置参数

支持下列参数：

| 编号 | 代码 | 参数 | 说明  |
| ---- | ---- | ---- | ---- |
| 1 | db | 自定义 |连接的数据库名，位于mg-db标签|
| 2 | table | 自定义 |操作的集合名，位于mg-table标签|
| 3 | tagValue | 自定义 |返回的标志|
| 4 | filter | 子标签 |用于对集合中的文档进行过滤|

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

> 过滤条件：统计"name"="Burger Buns"的文档数量

输出结果：
```
{
    "count": 3
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
				<mg-count tag="count">
					<filter module="Eq" field="name" value="Burger Buns"></filter>
				</mg-count>
			</mg-table>
		</mg-db>
	</mg-cli>
</script> 

```