mg-delete.md
=======

mg-delete.根据子标签<filter/>组合出的更新条件删除集合中一个或多个document，更新结果返回到当前文档tag的值指定的节点。

### 实现类

com.alogic.xscript.mongodb.MgDelete

### 配置参数

支持下列参数：

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | pid | mongodb的集合id，缺省为$mg-table |
| 2 | tag | 当前文档的节点,支持计算,缺省为$mg-delete |
| 6 | many | true,表示插入多个文档；false表示插入单个文档；缺省为false |



### 案例

现有下列脚本

```xml
<script>
	<using xmlTag="mg-table" module="com.alogic.xscript.mongodb.MgTable" />

	<mg-table cli="globalMongoDBClientPool" db="demo" table="test" >
		<mg-delete many="false">
		</mg-delete>
	</mg-table>

	<mg-table cli="globalMongoDBClientPool" db="demo" table="test" tag="delete2">
		<mg-delete many="false">
			<filter module="And">
				<filter module="Eq" field="a" value="aa-cli"></filter>
				<filter module="Eq" field="num" value="305" type="integer"></filter>
			</filter>
		</mg-delete>
	</mg-table>

</script>
```

结果

```json
	{"$mg-delete":{"deletedCount":0}}
```
