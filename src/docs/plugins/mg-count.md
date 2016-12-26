mg-count
======

mg-count：统计，用于统计查询出来的文档数量。

> 本指令对应mongodb的操作指令[count]()

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
				<mg-count tagValue="count">
					<filter module="Eq" field="name" value="Burger Buns"></filter>
				</mg-count>
			</mg-table>
		</mg-db>
	</mg-cli>
</script> 

```