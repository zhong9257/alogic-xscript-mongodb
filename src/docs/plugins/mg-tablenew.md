mg-tablenew
======

mg-tablenew：创建集合，用于在指定数据库中创建集合或固定集合。

> 概念说明：
> * 普通集合:随着数据容量的增加，会自动扩容；
> * 固定集合：其大小是固定的，当容量满了的时候，最新的文档会代替最老的文档。固定集合不能被分片。

> 本指令对应mongodb的操作指令[createCollection](http://mongodb.github.io/mongo-java-driver/3.4/driver/tutorials/databases-collections/)

### 实现类

com.alogic.xscript.mongodb.MgTableNew

### 配置参数

支持下列参数：

| 编号 | 代码 | 参数 | 说明  |
| ---- | ---- | ---- | ---- |
| 1 | db | 自定义 |连接的数据库名，位于mg-db标签|
| 2 | table | 自定义 |要创建的集合的名称|
| 3 | capped | true,false |是否创建固定集合|
| 4 | size | long |capped为true时使用，设置固定集合的大小，单位是byte|

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
			<mg-tablenew table="gg" capped="true" size="10000">
			</mg-tablenew>
		</mg-db>
	</mg-cli>
</script> 


```