mg-textsearch
======

mg-textsearch：文本搜索，用于对集合中的任意文本进行搜索，返回匹配到的文档，返回的文档按照相关度排序。

> 本指令对应mongodb的操作指令[createCollection](http://mongodb.github.io/mongo-java-driver/3.4/driver/tutorials/text-search/)

### 实现类

com.alogic.xscript.mongodb.MgTextSearch

### 配置参数

支持下列参数：

| 编号 | 代码 | 参数 | 说明  |
| ---- | ---- | ---- | ---- |
| 1 | db | 自定义 |连接的数据库名，位于mg-db标签|
| 2 | table | 自定义 |操作的集合名，位于mg-table标签|
| 3 | tagValue | 自定义 |返回的标志|
| 4 | keywords | 自定义 |搜索的关键字|
| 5 | textscore | true,false |收否返回计算的相关度|
| 6 | limit | 整数 |指定返回文档的数量|
| 7 | offset | 整数 |跳过文档的数量|

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
				<mg-textsearch tagValue="data" keywords="Shopping Java" textscore="true" limit="2" offset="1">
				</mg-textsearch>
			</mg-table>
		</mg-db>
	</mg-cli>
</script> 

```